package com.stokia.aura.domain.model

/**
 * Represents all possible authentication states in the app.
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: AuraUser) : AuthState()
    data object NeedsProfile : AuthState()
    data class Error(val message: String) : AuthState()
    data object Unauthenticated : AuthState()
}
