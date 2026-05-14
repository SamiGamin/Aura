package com.stokia.aura.presentation.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import com.stokia.aura.domain.model.AuthState
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<AuraUser>>(emptyList())
    val contacts: StateFlow<List<AuraUser>> = _contacts.asStateFlow()

    private val _currentUser = MutableStateFlow<AuraUser?>(null)
    val currentUser: StateFlow<AuraUser?> = _currentUser.asStateFlow()

    private val _addContactResult = MutableStateFlow<AuraResult<Unit>?>(null)
    val addContactResult: StateFlow<AuraResult<Unit>?> = _addContactResult.asStateFlow()

    private val _searchResult = MutableStateFlow<AuraResult<AuraUser>?>(null)
    val searchResult: StateFlow<AuraResult<AuraUser>?> = _searchResult.asStateFlow()

    init {
        observeCurrentUser()
        observeContacts()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.observeAuthState().collectLatest { state ->
                if (state is AuthState.Authenticated) {
                    _currentUser.value = state.user
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    private fun observeContacts() {
        viewModelScope.launch {
            contactRepository.observeContacts().collectLatest { list ->
                _contacts.value = list
            }
        }
    }

    fun searchUserByUsername(username: String) {
        viewModelScope.launch {
            _searchResult.value = AuraResult.Loading
            val result = contactRepository.searchUserByUsername(username)
            _searchResult.value = result
        }
    }

    fun addContactByUid(uid: String) {
        viewModelScope.launch {
            _addContactResult.value = AuraResult.Loading
            val result = contactRepository.addContact(uid)
            _addContactResult.value = result
        }
    }

    fun resetAddContactState() {
        _addContactResult.value = null
        _searchResult.value = null
    }
}
