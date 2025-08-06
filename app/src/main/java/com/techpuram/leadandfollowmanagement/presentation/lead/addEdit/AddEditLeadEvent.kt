package com.techpuram.leadandfollowmanagement.presentation.lead.addEdit

sealed class AddEditLeadEvent {
    data class EnteredName(val value: String) : AddEditLeadEvent()
    data class EnteredEmail(val value: String) : AddEditLeadEvent()
    data class EnteredMobile(val value: String) : AddEditLeadEvent()
    data class EnteredWhatsappNumber(val value: String) : AddEditLeadEvent()
    data class EnteredStatus(val value: String) : AddEditLeadEvent()
    data class EnteredLeadSource(val value: String) : AddEditLeadEvent()
    data class ToggleConverted(val value: Boolean) : AddEditLeadEvent()
    data class EnteredCustomField(val columnName: String, val value: String) : AddEditLeadEvent()
    data class ContactSelected(val id: Int, val name: String) : AddEditLeadEvent()
    object SaveLead : AddEditLeadEvent()


    data object ToggleConvertDialog : AddEditLeadEvent()
    data object ToggleCreateContact : AddEditLeadEvent()
    data class EnteredDealTitle(val value: String) : AddEditLeadEvent()
    data class EnteredDealAmount(val value: String) : AddEditLeadEvent()
    data class EnteredDealStage(val value: String) : AddEditLeadEvent()
    data class EnteredDealClosingDate(val value: Long) : AddEditLeadEvent()
    data class EnteredDealProbability(val value: String) : AddEditLeadEvent()
    data class EnteredDealDescription(val value: String) : AddEditLeadEvent()
    data object ConvertLeadToDeal : AddEditLeadEvent()

    data object EmptySelectedContact : AddEditLeadEvent()

} 