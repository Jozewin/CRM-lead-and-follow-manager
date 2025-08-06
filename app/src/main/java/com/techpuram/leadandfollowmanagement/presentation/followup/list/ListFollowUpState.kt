package com.techpuram.leadandfollowmanagement.presentation.followup.list

import com.techpuram.leadandfollowmanagement.domain.model.FollowUp

data class FollowUpListState(
    val isLoading: Boolean = false,
    val followUps: List<FollowUp> = emptyList(),
    val error: String? = null,
    val filterType: String = "All", // For filtering follow-ups
    val sortOrder: SortOrder = SortOrder.DUE_DATE_ASC
)

enum class SortOrder {
    DUE_DATE_ASC,
    DUE_DATE_DESC,
    PRIORITY_ASC,
    PRIORITY_DESC
} 