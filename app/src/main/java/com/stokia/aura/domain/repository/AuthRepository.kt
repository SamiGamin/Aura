package com.stokia.aura.domain.repository

import android.content.Context
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import kotlinx.coroutines.flow.Flow

/**
 * Auth repository contract. Data layer must implement this.
 */
interface AuthRepository {

    /** Observe the current Firebase Auth user as a Flow. */
    fun observeAuthState(): Flow<AuraUser?>

    /** Sign in with email and password. */
    suspend fun loginWithEmail(email: String, password: String): AuraResult<AuraUser>

    /** Create account with email and password. */
    suspend fun registerWithEmail(email: String, password: String): AuraResult<AuraUser>

    /** Sign in with Google Credential Manager. */
    suspend fun loginWithGoogle(context: Context): AuraResult<AuraUser>

    /** Send password reset email. */
    suspend fun sendPasswordReset(email: String): AuraResult<Unit>

    /** Sign out from all providers. */
    suspend fun signOut()

    /** Get the current user ID if authenticated, null otherwise. */
    fun getCurrentUserId(): String?
}
