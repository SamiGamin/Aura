package com.stokia.aura.presentation.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuthState
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel handling authentication state and operations.
 * Manages login, register, Google sign-in, and password reset.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Form fields
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _passwordResetSent = MutableStateFlow(false)
    val passwordResetSent: StateFlow<Boolean> = _passwordResetSent.asStateFlow()

    init {
        checkCurrentSession()
    }

    /**
     * Check if a Firebase session already exists on app start.
     */
    private fun checkCurrentSession() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { user ->
                if (user != null) {
                    val hasProfile = userRepository.hasCompletedProfile(user.uid)
                    _authState.value = if (hasProfile) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.NeedsProfile
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }

    fun login() {
        val emailVal = _email.value.trim()
        val passVal = _password.value
        if (emailVal.isBlank() || passVal.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.loginWithEmail(emailVal, passVal)) {
                is AuraResult.Success -> {
                    // Auth state listener will handle the transition
                }
                is AuraResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun register() {
        val emailVal = _email.value.trim()
        val passVal = _password.value
        val confirmVal = _confirmPassword.value
        if (emailVal.isBlank() || passVal.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        if (passVal != confirmVal) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }
        if (passVal.length < 8) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.registerWithEmail(emailVal, passVal)) {
                is AuraResult.Success -> {
                    // Auth state listener will handle the transition
                }
                is AuraResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.loginWithGoogle(context)) {
                is AuraResult.Success -> {
                    // Check if profile is complete. If not, auto-save the Google data to Firestore
                    val hasProfile = userRepository.hasCompletedProfile(result.data.uid)
                    if (!hasProfile) {
                        // Pre-save the Google data (displayName, photoUrl) so CreateProfile can show it
                        userRepository.saveUser(result.data)
                    }
                    // Auth state listener will handle the transition (NeedsProfile or Authenticated)
                }
                is AuraResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun sendPasswordReset() {
        val emailVal = _email.value.trim()
        if (emailVal.isBlank()) {
            _authState.value = AuthState.Error("Ingresa tu correo electrónico")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (authRepository.sendPasswordReset(emailVal)) {
                is AuraResult.Success -> {
                    _passwordResetSent.value = true
                    _authState.value = AuthState.Unauthenticated
                }
                is AuraResult.Failure -> {
                    _authState.value = AuthState.Error("Error al enviar email de recuperación")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}
