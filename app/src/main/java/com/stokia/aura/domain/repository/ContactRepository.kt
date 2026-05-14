package com.stokia.aura.domain.repository

import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for managing the user's contacts.
 */
interface ContactRepository {

    /**
     * Searches for a user by their unique @username.
     * @param username The exact username (without '@').
     * @return AuraResult containing the AuraUser if found, or an error.
     */
    suspend fun searchUserByUsername(username: String): AuraResult<AuraUser>

    /**
     * Adds a user to the current user's contact list.
     * @param targetUid The UID of the user to add.
     * @return AuraResult indicating success or failure.
     */
    suspend fun addContact(targetUid: String): AuraResult<Unit>

    /**
     * Observes the current user's list of confirmed contacts.
     * This will emit updates in real-time as the contact list changes.
     */
    fun observeContacts(): Flow<List<AuraUser>>
}
