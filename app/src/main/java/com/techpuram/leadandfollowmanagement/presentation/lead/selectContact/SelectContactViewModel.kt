package com.techpuram.leadandfollowmanagement.presentation.lead.selectContact

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
class SelectContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private var allContacts = listOf<Contact>()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            contactRepository.getAllContacts().collect { contactList ->
                allContacts = contactList
                _contacts.value = contactList
            }
        }
    }

    fun searchContacts(query: String) {
        if (query.isBlank()) {
            _contacts.value = allContacts
            return
        }

        _contacts.value = allContacts.filter { contact ->
            contact.name.contains(query, ignoreCase = true) ||
                    contact.mobile.contains(query, ignoreCase = true) ||
                    (contact.email?.contains(query, ignoreCase = true) ?: false)
        }
    }
}