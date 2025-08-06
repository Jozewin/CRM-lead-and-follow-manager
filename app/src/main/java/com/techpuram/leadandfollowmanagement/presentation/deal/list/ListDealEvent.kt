package com.techpuram.leadandfollowmanagement.presentation.deal.list

sealed class ListDealEvent {
    data class FilterByStage(val stage: String?) : ListDealEvent()
    data class UpdateSort(val sortOrder: DealSortOrder) : ListDealEvent()
    data class DeleteDeal(val dealId: Int) : ListDealEvent()
    data class UpdateDealStage(val dealId: Int, val newStage: String) : ListDealEvent()
    object RefreshDeals : ListDealEvent()
    object ClearError : ListDealEvent()
} 