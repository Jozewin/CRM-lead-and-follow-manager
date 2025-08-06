package com.techpuram.leadandfollowmanagement.presentation.contact.addEdit

sealed class AddEditContactEvent {
    data class EnteredName(val value: String) : AddEditContactEvent()
    data class EnteredEmail(val value: String?) : AddEditContactEvent()
    data class EnteredMobile(val value: String) : AddEditContactEvent()
    data class EnteredWhatsappNumber(val value: String?) : AddEditContactEvent()
    data class EnteredCompanyName(val value: String?) : AddEditContactEvent()
    data class EnteredLeadId(val value: String?) : AddEditContactEvent()
    data class EnteredAdditionalMobile(val value: String?) : AddEditContactEvent()
    data class EnteredStreet(val value: String?) : AddEditContactEvent()
    data class EnteredState(val value: String?) : AddEditContactEvent()
    data class EnteredCity(val value: String?) : AddEditContactEvent()
    data class EnteredCountry(val value: String?) : AddEditContactEvent()
    data class EnteredZip(val value: String?) : AddEditContactEvent()
    data class EnteredNote(val value: String?) : AddEditContactEvent()
    data class PickedPhoto(val uri: String?) : AddEditContactEvent()
    data object SaveContact : AddEditContactEvent()
}
