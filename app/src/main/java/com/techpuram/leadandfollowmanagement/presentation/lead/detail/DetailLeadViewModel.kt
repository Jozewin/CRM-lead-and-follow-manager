package com.techpuram.leadandfollowmanagement.presentation.lead.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.model.Lead
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailLeadViewModel @Inject constructor(
    private val leadRepository: LeadRepository,
    private val contactRepository: ContactRepository,
    private val customFieldRepository: CustomFieldRepository
) : ViewModel() {

    private val _leadDetail = MutableStateFlow<Lead?>(null)
    val leadDetail: StateFlow<Lead?> = _leadDetail.asStateFlow()

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact.asStateFlow()

    private val _customFields = MutableStateFlow<List<CustomField>>(emptyList())
    val customFields: StateFlow<List<CustomField>> = _customFields.asStateFlow()

    init {
        viewModelScope.launch {
            customFieldRepository.getCustomFieldsByModule("Lead").collect { fields ->
                _customFields.value = fields
            }
        }
    }

    fun getLeadById(leadId: Int) {
        viewModelScope.launch {
            val lead = leadRepository.getLeadById(leadId)
            _leadDetail.value = lead
            
            // Load associated contact if exists
            lead?.contactId?.let { contactId ->
                _contact.value = contactRepository.getContactById(contactId)
            }
        }
    }

    fun deleteLead(lead: Lead) {
        viewModelScope.launch {
            leadRepository.deleteLead(lead)
        }
    }
} 