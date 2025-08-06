package com.techpuram.leadandfollowmanagement.presentation.contact.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val customFieldRepository: CustomFieldRepository
) : ViewModel(){
    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact

    private val _customFields = MutableStateFlow<List<CustomField>>(emptyList())
    val customFields: StateFlow<List<CustomField>> = _customFields.asStateFlow()

    init {
        viewModelScope.launch {
            customFieldRepository.getCustomFieldsByModule("Contact").collect { fields ->
                _customFields.value = fields
            }
        }
    }

    fun getContactId(contactId: Int){
        viewModelScope.launch {
            _contact.value = repository.getContactById(contactId)
        }
    }
    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            try {
                repository.deleteContact(contact)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }


}