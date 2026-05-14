package com.stokia.aura.domain.repository

import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser

/**
 * User repository contract for profile CRUD operations in Firestore.
 */
interface UserRepository {

    /** Get a user profile by UID. */
    suspend fun getUserById(uid: String): AuraResult<AuraUser>

    /** Create or update a user profile in Firestore. */
    suspend fun saveUser(user: AuraUser): AuraResult<Unit>

    /** Check if a username is already taken. */
    suspend fun isUsernameTaken(username: String): Boolean

    /** Reserve a username for a user (atomic write). */
    suspend fun reserveUsername(username: String, uid: String): AuraResult<Unit>

    /** Check if the user has completed profile setup. */
    suspend fun hasCompletedProfile(uid: String): Boolean
}
