package com.stokia.aura.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.domain.repository.ContactRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ContactRepository {

    override suspend fun searchUserByUsername(username: String): AuraResult<AuraUser> {
        return try {
            val cleanUsername = username.removePrefix("@").lowercase().trim()
            
            // 1. Obtener el UID asociado al username
            val usernameDoc = firestore.collection("usernames").document(cleanUsername).get().await()
            if (!usernameDoc.exists()) {
                return AuraResult.Error(Exception("Usuario no encontrado"))
            }
            
            val targetUid = usernameDoc.getString("userId") ?: return AuraResult.Error(Exception("Datos corruptos en username"))
            
            // 2. Obtener el perfil del usuario
            val userDoc = firestore.collection("users").document(targetUid).get().await()
            val auraUser = userDoc.toObject(AuraUser::class.java)
                ?: return AuraResult.Error(Exception("Perfil no encontrado"))
                
            AuraResult.Success(auraUser)
        } catch (e: Exception) {
            AuraResult.Error(e)
        }
    }

    override suspend fun addContact(targetUid: String): AuraResult<Unit> {
        return try {
            val myUid = authRepository.getCurrentUserUid() 
                ?: return AuraResult.Error(Exception("No autenticado"))
                
            if (myUid == targetUid) {
                return AuraResult.Error(Exception("No puedes agregarte a ti mismo"))
            }

            val contactData = mapOf(
                "addedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(myUid)
                .collection("contacts")
                .document(targetUid)
                .set(contactData)
                .await()

            AuraResult.Success(Unit)
        } catch (e: Exception) {
            AuraResult.Error(e)
        }
    }

    override fun observeContacts(): Flow<List<AuraUser>> = callbackFlow {
        val myUid = authRepository.getCurrentUserUid()
        if (myUid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(myUid)
            .collection("contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Extraemos los UIDs de nuestros contactos
                val contactUids = snapshot.documents.map { it.id }

                // TODO: En producción con más de 30 contactos, se debe usar chunks de 30 o escuchar cambios individuales.
                // Para simplificar la demo actual, consultamos todos si son menos de 30, o solo tomamos los primeros 30.
                val uidsToFetch = contactUids.take(30)
                
                if (uidsToFetch.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Hacemos un "where-in" para traer los perfiles completos
                firestore.collection("users")
                    .whereIn("uid", uidsToFetch)
                    .get()
                    .addOnSuccessListener { usersSnapshot ->
                        val users = usersSnapshot.documents.mapNotNull { it.toObject(AuraUser::class.java) }
                        trySend(users)
                    }
                    .addOnFailureListener {
                        trySend(emptyList())
                    }
            }

        awaitClose { listener.remove() }
    }
}
