package com.techpuram.leadandfollowmanagement.presentation.followup.detail

import com.techpuram.leadandfollowmanagement.domain.model.FollowUp

data class DetailFollowUpState(
    val followUp: FollowUp? = null,
    val recordName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleted: Boolean = false
) 