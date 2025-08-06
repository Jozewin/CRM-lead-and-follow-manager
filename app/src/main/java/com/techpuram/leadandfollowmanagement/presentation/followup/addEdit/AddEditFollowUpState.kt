package com.techpuram.leadandfollowmanagement.presentation.followup.addEdit

data class AddEditFollowUpState(
    val id: Int? = null,
    val module: String = "Contact",
    val recordId: Int? = null,
    val recordName: String = "",
    val hasDueTime: Boolean = false,
    val dueTime: Long? = System.currentTimeMillis() + 24 * 60 * 60 * 1000,
    val notes: String = "",
    val followUpStage: String = "scheduled",
    val followUpType: String = "",
    val priority: String = "Medium",
    val reminder: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,

    // Dropdown options
    val availableModules: List<String> = listOf("Contact", "Lead", "Deal"),
    val availableStages: List<String> = listOf("Scheduled", "In-progress", "Completed"),
    val availableTypes: List<String> = listOf("Call", "Meeting", "In-person", "Message"),
    val availablePriorities: List<String> = listOf("Low", "Medium", "High"),
    val availableReminders: List<Pair<Long, String>> = listOf(
        1L to "1 minute before",
        5L to "5 minutes before",
        10L to "10 minutes before",
        15L to "15 minutes before",
        30L to "30 minutes before"
    ),


)