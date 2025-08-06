package com.techpuram.leadandfollowmanagement.presentation.deal.addEdit

sealed class AddEditDealEvent {
    data class TitleChanged(val title: String) : AddEditDealEvent()
    data class AmountChanged(val amount: String) : AddEditDealEvent()
    data class DescriptionChanged(val description: String) : AddEditDealEvent()
    data class StageChanged(val stage: String) : AddEditDealEvent()
    data class ProbabilityChanged(val probability: String) : AddEditDealEvent()
    data class ClosingDateChanged(val timestamp: Long) : AddEditDealEvent()
    data class ContactSelected(val id: Int, val name: String) : AddEditDealEvent()
    data object SaveDeal : AddEditDealEvent()
    data object ClearError : AddEditDealEvent()
    data class CustomFieldValueChanged(val fieldId: String, val value: String) : AddEditDealEvent()
} 