package com.techpuram.leadandfollowmanagement.presentation.followup.list

sealed class ListFollowUpEvent {
    data class FilterChanged(val filterType: String) : ListFollowUpEvent()
    data class SortOrderChanged(val sortOrder: SortOrder) : ListFollowUpEvent()
    data class DeleteFollowUp(val followUpId: Int) : ListFollowUpEvent()
    data class UpdateFollowUpStage(val followUpId: Int, val newStage: String) : ListFollowUpEvent()
    object RefreshFollowUps : ListFollowUpEvent()
    object ClearError : ListFollowUpEvent()
} 