package com.stokia.aura.presentation.screens.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    // Local URI from gallery/camera picker for Avatar
    private val _localAvatarUri = MutableStateFlow<Uri?>(null)
    val localAvatarUri: StateFlow<Uri?> = _localAvatarUri.asStateFlow()

    // Local URI from gallery/camera picker for Cover Image
    private val _localCoverUri = MutableStateFlow<Uri?>(null)
    val localCoverUri: StateFlow<Uri?> = _localCoverUri.asStateFlow()

    // URL from Google (if any)
    private var googlePhotoUrl: String = ""
    private var googleCoverUrl: String = ""
    private var uid: String = ""

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            uid = authRepository.getCurrentUserId() ?: run {
                _uiState.value = ProfileUiState.Error("No hay sesión activa")
                return@launch
            }

            when (val result = userRepository.getUserById(uid)) {
                is AuraResult.Success -> {
                    val user = result.data
                    _displayName.value = user.displayName
                    googlePhotoUrl = user.photoUrl
                    googleCoverUrl = user.coverUrl
                    _uiState.value = ProfileUiState.Idle
                }
                is AuraResult.Failure -> {
                    // Profile document might not exist yet if registered by email only
                    _uiState.value = ProfileUiState.Idle
                }
            }
        }
    }

    fun onUsernameChange(value: String) {
        // Only allow alphanumeric and underscores
        val filtered = value.filter { it.isLetterOrDigit() || it == '_' }.lowercase()
        _username.value = filtered
    }

    fun onDisplayNameChange(value: String) {
        val capitalized = value.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
        _displayName.value = capitalized
    }

    fun onAvatarSelected(uri: Uri?) {
        _localAvatarUri.value = uri
    }

    fun onCoverSelected(uri: Uri?) {
        _localCoverUri.value = uri
    }

    fun saveProfile() {
        val currentUsername = _username.value.trim()
        val currentDisplayName = _displayName.value.trim()

        if (currentUsername.length < 3) {
            _uiState.value = ProfileUiState.Error("El nombre de usuario debe tener al menos 3 caracteres")
            return
        }

        if (currentDisplayName.isBlank()) {
            _uiState.value = ProfileUiState.Error("El nombre a mostrar es obligatorio")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Saving("Verificando nombre de usuario...")
            
            // 1. Reserve username
            val reserveResult = userRepository.reserveUsername(currentUsername, uid)
            if (reserveResult is AuraResult.Failure) {
                _uiState.value = ProfileUiState.Error(reserveResult.message)
                return@launch
            }

            // 2. Upload avatar if a new one was selected
            var finalPhotoUrl = googlePhotoUrl
            val uriToUpload = _localAvatarUri.value
            if (uriToUpload != null) {
                _uiState.value = ProfileUiState.Saving("Subiendo avatar...")
                val uploadUrl = uploadToCloudinary(uriToUpload)
                if (uploadUrl != null) {
                    finalPhotoUrl = uploadUrl
                } else {
                    _uiState.value = ProfileUiState.Error("Error al subir el avatar. Intenta de nuevo.")
                    return@launch
                }
            }

            // 3. Upload cover if a new one was selected
            var finalCoverUrl = googleCoverUrl
            val coverUriToUpload = _localCoverUri.value
            if (coverUriToUpload != null) {
                _uiState.value = ProfileUiState.Saving("Subiendo portada...")
                val uploadUrl = uploadToCloudinary(coverUriToUpload)
                if (uploadUrl != null) {
                    finalCoverUrl = uploadUrl
                } else {
                    _uiState.value = ProfileUiState.Error("Error al subir la portada. Intenta de nuevo.")
                    return@launch
                }
            }

            // 4. Save full user profile
            _uiState.value = ProfileUiState.Saving("Guardando perfil...")
            val email = authRepository.observeAuthState().firstOrNull()?.email ?: ""
            val user = AuraUser(
                uid = uid,
                email = email,
                username = currentUsername,
                displayName = currentDisplayName,
                photoUrl = finalPhotoUrl,
                coverUrl = finalCoverUrl,
                createdAt = System.currentTimeMillis()
            )

            when (val saveResult = userRepository.saveUser(user)) {
                is AuraResult.Success -> {
                    _uiState.value = ProfileUiState.Success
                }
                is AuraResult.Failure -> {
                    _uiState.value = ProfileUiState.Error(saveResult.message)
                }
            }
        }
    }

    private suspend fun uploadToCloudinary(uri: Uri): String? = suspendCoroutine { continuation ->
        try {
            MediaManager.get().upload(uri)
                .option("folder", "Aura/user/$uid")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val secureUrl = resultData?.get("secure_url") as? String
                        continuation.resume(secureUrl)
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        continuation.resume(null)
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }

    fun clearError() {
        if (_uiState.value is ProfileUiState.Error) {
            _uiState.value = ProfileUiState.Idle
        }
    }
}

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Saving(val message: String) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Success : ProfileUiState()
}
