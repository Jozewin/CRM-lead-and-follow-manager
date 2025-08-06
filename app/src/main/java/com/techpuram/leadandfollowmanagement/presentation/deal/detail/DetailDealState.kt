package com.techpuram.leadandfollowmanagement.presentation.deal.detail

import com.techpuram.leadandfollowmanagement.domain.model.Deal

data class DetailDealState(
    val deal: Deal? = null,
    val contactName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) 