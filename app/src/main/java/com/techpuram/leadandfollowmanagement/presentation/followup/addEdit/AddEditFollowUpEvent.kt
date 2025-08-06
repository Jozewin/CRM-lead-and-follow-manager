package com.techpuram.leadandfollowmanagement.presentation.followup.addEdit


sealed class AddEditFollowUpEvent {
    data class ModuleChanged(val module: String) : AddEditFollowUpEvent()
    data class RecordSelected(val id: Int, val name: String) : AddEditFollowUpEvent()
    data class DueTimeChanged(val timestamp: Long) : AddEditFollowUpEvent()
    data class NotesChanged(val notes: String) : AddEditFollowUpEvent()
    data class StageChanged(val stage: String) : AddEditFollowUpEvent()
    data class TypeChanged(val type: String) : AddEditFollowUpEvent()
    data class PriorityChanged(val priority: String) : AddEditFollowUpEvent()
    data class ReminderChanged(val minutes: Long?) : AddEditFollowUpEvent()
    data object SaveFollowUp : AddEditFollowUpEvent()
    data object SelectRecord : AddEditFollowUpEvent()
    data class HasDueTimeChanged(val hasDueTime: Boolean) : AddEditFollowUpEvent()
}