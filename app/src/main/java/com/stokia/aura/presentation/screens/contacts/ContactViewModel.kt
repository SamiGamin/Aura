package com.stokia.aura.presentation.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.domain.model.AuraUser
import com.stokia.aura.domain.model.AuthState
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.domain.repository.ContactRepository
import com.stokia.aura.domain.repository.UserRepository
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
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<AuraUser>>(emptyList())
    val contacts: StateFlow<List<AuraUser>> = _contacts.asStateFlow()

    private val _currentUser = MutableStateFlow<AuraUser?>(null)
    val currentUser: StateFlow<AuraUser?> = _currentUser.asStateFlow()

    private val _addContactResult = MutableStateFlow<AuraResult<Unit>?>(null)
    val addContactResult: StateFlow<AuraResult<Unit>?> = _addContactResult.asStateFlow()

    private val _searchResult = MutableStateFlow<AuraResult<AuraUser>?>(null)
    val searchResult: StateFlow<AuraResult<AuraUser>?> = _searchResult.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isAddingContact = MutableStateFlow(false)
    val isAddingContact: StateFlow<Boolean> = _isAddingContact.asStateFlow()

    init {
        observeCurrentUser()
        observeContacts()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.observeAuthState().collectLatest { authUser ->
                if (authUser != null) {
                    val result = userRepository.getUserById(authUser.uid)
                    if (result is AuraResult.Success) {
                        _currentUser.value = result.data
                    } else {
                        _currentUser.value = null
                    }
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
            _isSearching.value = true
            val result = contactRepository.searchUserByUsername(username)
            _searchResult.value = result
            _isSearching.value = false
        }
    }

    fun addContactByUid(uid: String) {
        viewModelScope.launch {
            _isAddingContact.value = true
            val result = contactRepository.addContact(uid)
            _addContactResult.value = result
            _isAddingContact.value = false
        }
    }

    fun resetAddContactState() {
        _addContactResult.value = null
        _searchResult.value = null
    }
}
