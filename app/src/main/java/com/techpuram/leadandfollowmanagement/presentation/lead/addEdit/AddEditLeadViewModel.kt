package com.techpuram.leadandfollowmanagement.presentation.lead.addEdit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.model.Deal
import com.techpuram.leadandfollowmanagement.domain.model.Lead
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditLeadViewModel @Inject constructor(
    private val leadRepository: LeadRepository,
    private val contactRepository: ContactRepository,
    private val customFieldRepository: CustomFieldRepository,
    private val dealRepository: DealRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _AddOrUpdate_leadState = MutableStateFlow(AddEditLeadState())
    val addOrUpdateLeadState: StateFlow<AddEditLeadState> = _AddOrUpdate_leadState.asStateFlow()

    private val _customFields = MutableStateFlow<List<CustomField>>(emptyList())
    val customFields: StateFlow<List<CustomField>> = _customFields.asStateFlow()

    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()
    
    private val _saveComplete = MutableStateFlow<Long?>(null)
    val saveComplete: StateFlow<Long?> = _saveComplete.asStateFlow()

    private val leadId: Int = savedStateHandle.get<Int>("leadId") ?: -1

    init {
        if (leadId != -1) {
            loadLead(leadId)
        }

        viewModelScope.launch {
            customFieldRepository.getCustomFieldsByModule("Lead").collect { fields ->
                _customFields.value = fields
            }
        }
    }

    fun onEvent(event: AddEditLeadEvent) {
        when (event) {
            is AddEditLeadEvent.EnteredName -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                    name = event.value,
                    nameError = null // Clear error when user types
                )
            }

            is AddEditLeadEvent.EnteredEmail -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(email = event.value)
            }

            is AddEditLeadEvent.EnteredMobile -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                    mobile = event.value,
                    mobileError = null // Clear error when user types
                )
            }

            is AddEditLeadEvent.EnteredWhatsappNumber -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(whatsappNumber = event.value)
            }

            is AddEditLeadEvent.EnteredStatus -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                    status = event.value,
                    statusError = null // Clear error when user selects status
                )
            }

            is AddEditLeadEvent.EnteredLeadSource -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(leadSource = event.value)
            }

            is AddEditLeadEvent.ToggleConverted -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(isConverted = event.value)
            }

            is AddEditLeadEvent.EnteredCustomField -> {
                updateCustomField(event.columnName, event.value)
            }

            is AddEditLeadEvent.SaveLead -> {
                saveLead()
            }

            is AddEditLeadEvent.ToggleConvertDialog -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                    showConvertDialog = !_AddOrUpdate_leadState.value.showConvertDialog
                )
            }
            
            is AddEditLeadEvent.ToggleCreateContact -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                    createContactFromLead = !_AddOrUpdate_leadState.value.createContactFromLead
                )
            }
            
            is AddEditLeadEvent.EnteredDealTitle -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(dealTitle = event.value)
            }
            
            is AddEditLeadEvent.EnteredDealAmount -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(dealAmount = event.value)
            }
            
            is AddEditLeadEvent.EnteredDealStage -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(dealStage = event.value)
            }
            
            is AddEditLeadEvent.EnteredDealClosingDate -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(dealClosingDate = event.value)
            }
            
            is AddEditLeadEvent.EnteredDealProbability -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(dealProbability = event.value)
            }
            
            is AddEditLeadEvent.EnteredDealDescription -> {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(dealDescription = event.value)
            }
            
            is AddEditLeadEvent.ConvertLeadToDeal -> {
                convertLeadToDeal()
            }

            is AddEditLeadEvent.ContactSelected -> {
                _AddOrUpdate_leadState.update {
                    it.copy(
                        contactId = if (event.id > 0) event.id else null,
                        contactName = event.name
                    )
                }
            }

            AddEditLeadEvent.EmptySelectedContact -> {
                _AddOrUpdate_leadState.update {
                    it.copy(
                        contactId = null,
                        contactName = null
                    )
                }
            }
        }
    }

    fun updateSelectedContact(contact: Contact) {
        _selectedContact.value = contact
        _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(contactId = contact.id)
    }

    private fun updateCustomField(columnName: String, value: String) {
        _AddOrUpdate_leadState.value = when (columnName) {
            "cf1" -> _AddOrUpdate_leadState.value.copy(cf1 = value)
            "cf2" -> _AddOrUpdate_leadState.value.copy(cf2 = value)
            "cf3" -> _AddOrUpdate_leadState.value.copy(cf3 = value)
            "cf4" -> _AddOrUpdate_leadState.value.copy(cf4 = value)
            "cf5" -> _AddOrUpdate_leadState.value.copy(cf5 = value)
            "cf6" -> _AddOrUpdate_leadState.value.copy(cf6 = value)
            "cf7" -> _AddOrUpdate_leadState.value.copy(cf7 = value)
            "cf8" -> _AddOrUpdate_leadState.value.copy(cf8 = value)
            "cf9" -> _AddOrUpdate_leadState.value.copy(cf9 = value)
            "cf10" -> _AddOrUpdate_leadState.value.copy(cf10 = value)
            "cf11" -> _AddOrUpdate_leadState.value.copy(cf11 = value)
            "cf12" -> _AddOrUpdate_leadState.value.copy(cf12 = value)
            "cf13" -> _AddOrUpdate_leadState.value.copy(cf13 = value)
            "cf14" -> _AddOrUpdate_leadState.value.copy(cf14 = value)
            "cf15" -> _AddOrUpdate_leadState.value.copy(cf15 = value)
            "cf16" -> _AddOrUpdate_leadState.value.copy(cf16 = value)
            "cf17" -> _AddOrUpdate_leadState.value.copy(cf17 = value)
            "cf18" -> _AddOrUpdate_leadState.value.copy(cf18 = value)
            "cf19" -> _AddOrUpdate_leadState.value.copy(cf19 = value)
            "cf20" -> _AddOrUpdate_leadState.value.copy(cf20 = value)
            else -> _AddOrUpdate_leadState.value
        }
    }

    private fun loadLead(leadId: Int) {
        viewModelScope.launch {
            leadRepository.getLeadById(leadId)?.let { lead ->
                _AddOrUpdate_leadState.value = AddEditLeadState(
                    id = lead.id,
                    name = lead.name,
                    contactId = lead.contactId,
                    isConverted = lead.isConverted,
                    email = lead.email,
                    mobile = lead.mobile,
                    status = lead.status,
                    whatsappNumber = lead.whatsappNumber,
                    leadSource = lead.leadSource,
                    createdTime = lead.createdTime,
                    modifiedTime = lead.modifiedTime,
                    cf1 = lead.cf1,
                    cf2 = lead.cf2,
                    cf3 = lead.cf3,
                    cf4 = lead.cf4,
                    cf5 = lead.cf5,
                    cf6 = lead.cf6,
                    cf7 = lead.cf7,
                    cf8 = lead.cf8,
                    cf9 = lead.cf9,
                    cf10 = lead.cf10,
                    cf11 = lead.cf11,
                    cf12 = lead.cf12,
                    cf13 = lead.cf13,
                    cf14 = lead.cf14,
                    cf15 = lead.cf15,
                    cf16 = lead.cf16,
                    cf17 = lead.cf17,
                    cf18 = lead.cf18,
                    cf19 = lead.cf19,
                    cf20 = lead.cf20,
                    prop = lead.prop
                )

                // Load associated contact if exists
                lead.contactId?.let { contactId ->
                    viewModelScope.launch {
                        contactRepository.getContactById(contactId)?.let { contact ->
                            _AddOrUpdate_leadState.update {
                              it.copy(
                                  contactName = contact.name
                              )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveLead() {
        val state = _AddOrUpdate_leadState.value
        
        // Clear previous errors
        _AddOrUpdate_leadState.value = state.copy(
            nameError = null,
            mobileError = null,
            statusError = null,
            generalError = null
        )
        
        // Validate required fields
        var hasError = false
        val updatedState = _AddOrUpdate_leadState.value.copy(
            nameError = if (state.name.isBlank()) {
                hasError = true
                "Name is required"
            } else null,
            mobileError = if (state.mobile.isBlank()) {
                hasError = true
                "Mobile number is required"
            } else null,
            statusError = if (state.status.isBlank()) {
                hasError = true
                "Please select a status"
            } else null
        )
        
        if (hasError) {
            _AddOrUpdate_leadState.value = updatedState
            return
        }

        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()

            val lead = Lead(
                id = state.id,
                name = state.name,
                contactId = if (state.contactId != null && state.contactId > 0) state.contactId else null,
                isConverted = state.isConverted,
                email = state.email,
                mobile = state.mobile,
                status = state.status,
                whatsappNumber = state.whatsappNumber,
                leadSource = state.leadSource,
                createdTime = state.createdTime ?: currentTime,
                modifiedTime = currentTime,
                cf1 = state.cf1,
                cf2 = state.cf2,
                cf3 = state.cf3,
                cf4 = state.cf4,
                cf5 = state.cf5,
                cf6 = state.cf6,
                cf7 = state.cf7,
                cf8 = state.cf8,
                cf9 = state.cf9,
                cf10 = state.cf10,
                cf11 = state.cf11,
                cf12 = state.cf12,
                cf13 = state.cf13,
                cf14 = state.cf14,
                cf15 = state.cf15,
                cf16 = state.cf16,
                cf17 = state.cf17,
                cf18 = state.cf18,
                cf19 = state.cf19,
                cf20 = state.cf20,
                prop = state.prop
            )

            if (state.id != null) {
                leadRepository.updateLead(lead)
                _saveComplete.value = leadId.toLong()
            } else {
                val newLeadId = leadRepository.insertLead(lead)
                _saveComplete.value = newLeadId
            }
        }
    }

    private fun convertLeadToDeal() {
        viewModelScope.launch {
            try {
                val currentState = _AddOrUpdate_leadState.value
                
                // Create contact if needed
                var contactId: Int? = null
                if (currentState.createContactFromLead) {
                    val contact = Contact(
                        name = currentState.name,
                        email = currentState.email,
                        mobile = currentState.mobile
                        // Map other relevant fields
                    )
                    contactId = contactRepository.insertContact(contact).toInt()

                }
                
                // Create deal
                val deal = Deal(
                    title = currentState.dealTitle,
                    amount = currentState.dealAmount.toDoubleOrNull(),
                    stage = currentState.dealStage,
                    closingDate = currentState.dealClosingDate,
                    probability = currentState.dealProbability.toIntOrNull(),
                    description = currentState.dealDescription,
                    contactId = contactId,
                    createdTime = System.currentTimeMillis(),
                    modifiedTime = System.currentTimeMillis()
                )
                dealRepository.insertDeal(deal)
                
                // Update or delete the lead
//                currentState.id?.let { leadId ->
//                    val lead = leadRepository.getLeadById(leadId)
//                    lead?.let {
//                        leadRepository.deleteLead(it)
//                    }
//                }
                
                // Close dialog and navigate back
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                    showConvertDialog = false
                   // isConverted = false
                )
                
            } catch (e: Exception) {
                _AddOrUpdate_leadState.value = _AddOrUpdate_leadState.value.copy(
                  //  error = e.message ?: "Failed to convert lead"
                )
            }
        }
    }
}