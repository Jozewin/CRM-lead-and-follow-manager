package com.techpuram.leadandfollowmanagement.presentation.contact.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListContactViewModel @Inject constructor(
    private val repository: ContactRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _selectedContacts = MutableStateFlow<Set<Contact>>(emptySet())
    val selectedContacts: StateFlow<Set<Contact>> = _selectedContacts

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode

    init {
        fetchContacts()
    }

    private fun fetchContacts() {
        viewModelScope.launch {
            repository.getAllContacts().collect { contactList ->
                _contacts.value = contactList
            }
        }
    }

    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            clearSelection()
        }
    }

    fun toggleContactSelection(contact: Contact) {
        val currentSelection = _selectedContacts.value.toMutableSet()
        if (currentSelection.contains(contact)) {
            currentSelection.remove(contact)
        } else {
            currentSelection.add(contact)
        }
        _selectedContacts.value = currentSelection

        if (currentSelection.isEmpty()) {
            _isSelectionMode.value = false
        }
    }

    private fun clearSelection() {
        _selectedContacts.value = emptySet()
    }

    fun deleteSelectedContacts() {
        viewModelScope.launch {
            _selectedContacts.value.forEach { contact ->
                repository.deleteContact(contact)
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }
}
