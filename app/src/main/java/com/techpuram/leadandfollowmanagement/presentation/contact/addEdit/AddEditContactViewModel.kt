package com.techpuram.leadandfollowmanagement.presentation.contact.addEdit

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import com.techpuram.leadandfollowmanagement.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val customFieldRepository: CustomFieldRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var contact by mutableStateOf<Contact?>(null)
        private set

    private val _contactState = MutableStateFlow(AddEditContactState())
    val contactState: StateFlow<AddEditContactState> = _contactState
    private val todoId: Int = savedStateHandle.get<Int>("contactId")!!
    
    private val _saveComplete = MutableStateFlow<Long?>(null)
    val saveComplete: StateFlow<Long?> = _saveComplete.asStateFlow()

    private val _customFields = MutableStateFlow<List<CustomField>>(emptyList())
    val customFields: StateFlow<List<CustomField>> = _customFields.asStateFlow()

    init {

        if (todoId != -1) {
            viewModelScope.launch {
                repository.getContactById(todoId)?.let { contact ->
                    _contactState.value = AddEditContactState(
                        id = contact.id,
                        name = contact.name,
                        email = contact.email,
                        mobile = contact.mobile,
                        whatsappNumber = contact.whatsappNumber,
                        companyName = contact.companyName,
                        leadId = contact.leadId,
                        additionalMobile = contact.additionalMobile,
                        street = contact.street,
                        state = contact.state,
                        city = contact.city,
                        country = contact.country,
                        zip = contact.zip,
                        note = contact.note,
                        photoUri = contact.photoId,
                        cf1 = contact.cf1,
                        cf2 = contact.cf2,
                        cf3 = contact.cf3,
                        cf4 = contact.cf4,
                        cf5 = contact.cf5,
                        cf6 = contact.cf6,
                        cf7 = contact.cf7,
                        cf8 = contact.cf8,
                        cf9 = contact.cf9,
                        cf10 = contact.cf10,
                        cf11 = contact.cf11,
                        cf12 = contact.cf12,
                        cf13 = contact.cf13,
                        cf14 = contact.cf14,
                        cf15 = contact.cf15,
                        cf16 = contact.cf16,
                        cf17 = contact.cf17,
                        cf18 = contact.cf18,
                        cf19 = contact.cf19,
                        cf20 = contact.cf20
                    )

                    this@AddEditContactViewModel.contact = contact

                }
            }
        }

        viewModelScope.launch {
            customFieldRepository.getCustomFieldsByModule("Contact").collect { fields ->
                _customFields.value = fields
            }
        }
    }

    fun onEvent(event: AddEditContactEvent) {
        _contactState.value = when (event) {
            is AddEditContactEvent.EnteredName -> contactState.value.copy(name = event.value)
            is AddEditContactEvent.EnteredEmail -> contactState.value.copy(email = event.value)
            is AddEditContactEvent.EnteredMobile -> contactState.value.copy(mobile = event.value)
            is AddEditContactEvent.EnteredWhatsappNumber -> contactState.value.copy(
                whatsappNumber = event.value
            )

            is AddEditContactEvent.EnteredCompanyName -> contactState.value.copy(companyName = event.value)
            is AddEditContactEvent.EnteredLeadId -> contactState.value.copy(leadId = event.value)
            is AddEditContactEvent.EnteredAdditionalMobile -> contactState.value.copy(
                additionalMobile = event.value
            )

            is AddEditContactEvent.EnteredStreet -> contactState.value.copy(street = event.value)
            is AddEditContactEvent.EnteredState -> contactState.value.copy(state = event.value)
            is AddEditContactEvent.EnteredCity -> contactState.value.copy(city = event.value)
            is AddEditContactEvent.EnteredCountry -> contactState.value.copy(country = event.value)
            is AddEditContactEvent.EnteredZip -> contactState.value.copy(zip = event.value)
            is AddEditContactEvent.EnteredNote -> contactState.value.copy(note = event.value)
            is AddEditContactEvent.PickedPhoto -> {
                val permanentPath = event.uri?.let { uri ->
                    ImageUtils.saveImageToInternalStorage(context, Uri.parse(uri))
                }
                contactState.value.copy(photoUri = permanentPath)
            }

            is AddEditContactEvent.SaveContact -> {
                saveContact()
                contactState.value // No state change, just triggers save
            }
        }
    }

    private fun saveContact() {
        val state = _contactState.value
        if (state.name.isBlank() || state.mobile.isBlank()) return

        if (todoId == -1) {
            val newContact = Contact(
                name = state.name,
                email = state.email,
                mobile = state.mobile,
                whatsappNumber = state.whatsappNumber,
                companyName = state.companyName,
                leadId = state.leadId,
                additionalMobile = state.additionalMobile,
                street = state.street,
                state = state.state,
                city = state.city,
                country = state.country,
                zip = state.zip,
                note = state.note,
                photoId = state.photoUri,
                createdTime = System.currentTimeMillis(),
                modifiedTime = System.currentTimeMillis(),
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
                prop = null
            )

            viewModelScope.launch {
                val newContactId = repository.insertContact(newContact)
                _saveComplete.value = newContactId
            }
        } else {
            val newContact = state.id?.let {
                Contact(
                    id = it,
                    name = state.name,
                    email = state.email,
                    mobile = state.mobile,
                    whatsappNumber = state.whatsappNumber,
                    companyName = state.companyName,
                    leadId = state.leadId,
                    additionalMobile = state.additionalMobile,
                    street = state.street,
                    state = state.state,
                    city = state.city,
                    country = state.country,
                    zip = state.zip,
                    note = state.note,
                    photoId = state.photoUri,
                    createdTime = System.currentTimeMillis(),
                    modifiedTime = System.currentTimeMillis(),
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
                    prop = null
                )
            }

            viewModelScope.launch {
                if (newContact != null) {
                    repository.updateContact(newContact)
                    _saveComplete.value = todoId.toLong()
                }
            }
        }
    }

    fun onCustomFieldValueChanged(columnName: String, value: String) {
        val state = _contactState.value

        val updatedState = when (columnName) {
            "cf1" -> state.copy(cf1 = value)
            "cf2" -> state.copy(cf2 = value)
            "cf3" -> state.copy(cf3 = value)
            "cf4" -> state.copy(cf4 = value)
            "cf5" -> state.copy(cf5 = value)
            "cf6" -> state.copy(cf6 = value)
            "cf7" -> state.copy(cf7 = value)
            "cf8" -> state.copy(cf8 = value)
            "cf9" -> state.copy(cf9 = value)
            "cf10" -> state.copy(cf10 = value)
            "cf11" -> state.copy(cf11 = value)
            "cf12" -> state.copy(cf12 = value)
            "cf13" -> state.copy(cf13 = value)
            "cf14" -> state.copy(cf14 = value)
            "cf15" -> state.copy(cf15 = value)
            "cf16" -> state.copy(cf16 = value)
            "cf17" -> state.copy(cf17 = value)
            "cf18" -> state.copy(cf18 = value)
            "cf19" -> state.copy(cf19 = value)
            "cf20" -> state.copy(cf20 = value)
            else -> state
        }

        _contactState.value = updatedState
    }

    fun getCustomFieldValue(columnName: String): String? {
        val state = _contactState.value

        return when (columnName) {
            "cf1" -> state.cf1
            "cf2" -> state.cf2
            "cf3" -> state.cf3
            "cf4" -> state.cf4
            "cf5" -> state.cf5
            "cf6" -> state.cf6
            "cf7" -> state.cf7
            "cf8" -> state.cf8
            "cf9" -> state.cf9
            "cf10" -> state.cf10
            "cf11" -> state.cf11
            "cf12" -> state.cf12
            "cf13" -> state.cf13
            "cf14" -> state.cf14
            "cf15" -> state.cf15
            "cf16" -> state.cf16
            "cf17" -> state.cf17
            "cf18" -> state.cf18
            "cf19" -> state.cf19
            "cf20" -> state.cf20
            else -> null
        }

    }
}
