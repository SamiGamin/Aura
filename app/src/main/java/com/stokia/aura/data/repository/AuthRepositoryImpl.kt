package com.stokia.aura.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.utils.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Auth implementation of [AuthRepository].
 * Handles email/password, Google Sign-In, and session management.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun observeAuthState(): Flow<AuraUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                trySend(
                    AuraUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                    )
                )
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun loginWithEmail(email: String, password: String): AuraResult<AuraUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuraResult.Failure("Usuario no encontrado")
            AuraResult.Success(
                AuraUser(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            )
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al iniciar sesión", e)
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): AuraResult<AuraUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuraResult.Failure("No se pudo crear la cuenta")
            AuraResult.Success(
                AuraUser(
                    uid = user.uid,
                    email = user.email ?: ""
                )
            )
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al registrarse", e)
        }
    }

    override suspend fun loginWithGoogle(context: Context): AuraResult<AuraUser> {
        return try {
            val credentialManager = CredentialManager.create(context)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(Constants.WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential !is CustomCredential || credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return AuraResult.Failure("Tipo de credencial no soportado")
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            
            val user = authResult.user ?: return AuraResult.Failure("Error con Google Sign-In")
            
            AuraResult.Success(
                AuraUser(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            )
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al conectar con Google", e)
        }
    }

    override suspend fun sendPasswordReset(email: String): AuraResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuraResult.Success(Unit)
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al enviar email", e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
}
