package com.techpuram.leadandfollowmanagement.presentation.deal.addEdit

data class AddEditDealState(
    val id: Int? = null,
    val title: String = "",
    val amount: String = "",
    val description: String = "",
    val stage: String = "Initial Contact",
    val probability: String = "",
    val closingDate: Long? = null,
    val contactId: Int? = null,
    val contactName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    
    // Available options
    val availableStages: List<String> = listOf(
        "Initial Contact",
        "Proposal",
        "Negotiation",
        "Due Diligence",
        "Closed Won",
        "Closed Lost"
    ),
    
    // Add custom field values map
    val cf1: String? = null,
    val cf2: String? = null,
    val cf3: String? = null,
    val cf4: String? = null,
    val cf5: String? = null,
    val cf6: String? = null,
    val cf7: String? = null,
    val cf8: String? = null,
    val cf9: String? = null,
    val cf10: String? = null,
    val cf11: String? = null,
    val cf12: String? = null,
    val cf13: String? = null,
    val cf14: String? = null,
    val cf15: String? = null,
    val cf16: String? = null,
    val cf17: String? = null,
    val cf18: String? = null,
    val cf19: String? = null,
    val cf20: String? = null)