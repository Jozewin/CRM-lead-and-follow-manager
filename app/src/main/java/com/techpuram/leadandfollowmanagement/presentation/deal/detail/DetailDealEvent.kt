package com.techpuram.leadandfollowmanagement.presentation.deal.detail

sealed class DetailDealEvent {
    data object DeleteDeal : DetailDealEvent()
    data object ClearError : DetailDealEvent()
} 