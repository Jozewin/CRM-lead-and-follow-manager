package com.techpuram.leadandfollowmanagement.presentation.deal.list

import com.techpuram.leadandfollowmanagement.domain.model.Deal

data class DealListState(
    val deals: List<DealWithInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedStage: String? = null,
    val sortOrder: DealSortOrder = DealSortOrder.MODIFIED_TIME_DESC
)

data class DealWithInfo(
    val deal: Deal,
    val recordName: String = "", // Contact or Lead name
    val recordType: String = "" // "Contact" or "Lead"
)

enum class DealSortOrder {
    MODIFIED_TIME_DESC,
    AMOUNT_ASC,
    AMOUNT_DESC,
    CLOSING_DATE_ASC,
    CLOSING_DATE_DESC
} 