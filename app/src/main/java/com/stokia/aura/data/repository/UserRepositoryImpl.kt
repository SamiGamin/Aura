package com.stokia.aura.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import com.stokia.aura.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation of [UserRepository].
 * Manages user profiles and username uniqueness.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val USERNAMES_COLLECTION = "usernames"
    }

    override suspend fun getUserById(uid: String): AuraResult<AuraUser> {
        return try {
            val doc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            if (doc.exists()) {
                val user = AuraUser(
                    uid = uid,
                    email = doc.getString("email") ?: "",
                    username = doc.getString("username") ?: "",
                    displayName = doc.getString("displayName") ?: "",
                    photoUrl = doc.getString("photoUrl") ?: "",
                    publicKey = doc.getString("publicKey") ?: "",
                    fcmToken = doc.getString("fcmToken") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
                AuraResult.Success(user)
            } else {
                AuraResult.Failure("Perfil no encontrado")
            }
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al obtener perfil", e)
        }
    }

    override suspend fun saveUser(user: AuraUser): AuraResult<Unit> {
        return try {
            val data = hashMapOf(
                "email" to user.email,
                "username" to user.username,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl,
                "publicKey" to user.publicKey,
                "fcmToken" to user.fcmToken,
                "createdAt" to user.createdAt
            )
            firestore.collection(USERS_COLLECTION).document(user.uid).set(data).await()
            AuraResult.Success(Unit)
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al guardar perfil", e)
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val doc = firestore.collection(USERNAMES_COLLECTION).document(username.lowercase()).get().await()
            doc.exists()
        } catch (_: Exception) {
            true // Assume taken on error for safety
        }
    }

    override suspend fun reserveUsername(username: String, uid: String): AuraResult<Unit> {
        return try {
            val lowerUsername = username.lowercase()
            // Atomic check: if doc already exists, this will overwrite — we check first
            if (isUsernameTaken(lowerUsername)) {
                return AuraResult.Failure("El nombre de usuario '$username' ya está en uso")
            }
            val data = hashMapOf("userId" to uid)
            firestore.collection(USERNAMES_COLLECTION).document(lowerUsername).set(data).await()
            AuraResult.Success(Unit)
        } catch (e: Exception) {
            AuraResult.Failure(e.localizedMessage ?: "Error al reservar username", e)
        }
    }

    override suspend fun hasCompletedProfile(uid: String): Boolean {
        return try {
            val doc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            doc.exists() && !doc.getString("username").isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }
}
