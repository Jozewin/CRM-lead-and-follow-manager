package com.techpuram.leadandfollowmanagement.presentation.deal.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDealViewModel @Inject constructor(
    private val dealRepository: DealRepository,
    private val contactRepository: ContactRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DealListState())
    val state: StateFlow<DealListState> = _state.asStateFlow()

    init {
        loadDeals()
    }

    fun onEvent(event: ListDealEvent) {
        when (event) {
            is ListDealEvent.FilterByStage -> {
                _state.update { it.copy(selectedStage = event.stage) }
                loadDeals()
            }
            is ListDealEvent.UpdateSort -> {
                _state.update { it.copy(sortOrder = event.sortOrder) }
                loadDeals()
            }
            is ListDealEvent.DeleteDeal -> {
                deleteDeal(event.dealId)
            }
            is ListDealEvent.UpdateDealStage -> {
                updateDealStage(event.dealId, event.newStage)
            }
            ListDealEvent.RefreshDeals -> {
                loadDeals()
            }
            ListDealEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadDeals() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val dealsFlow = if (_state.value.selectedStage != null) {
                    dealRepository.getDealsByStage(_state.value.selectedStage!!)
                } else {
                    dealRepository.getAllDeals()
                }

                dealsFlow.collect { deals ->
                    val dealsWithInfo = deals.map { deal ->
                        val recordName = when {
                            deal.contactId != null -> {
                                val contact = contactRepository.getContactById(deal.contactId)
                                DealWithInfo(
                                    deal = deal,
                                    recordName = contact?.name ?: "Unknown Contact",
                                    recordType = "Contact"
                                )
                            }
                            else -> DealWithInfo(deal = deal)
                        }
                        recordName
                    }.sortedWith(getSortComparator(_state.value.sortOrder))

                    _state.update { 
                        it.copy(
                            deals = dealsWithInfo,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = e.message ?: "Failed to load deals",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun deleteDeal(dealId: Int) {
        viewModelScope.launch {
            try {
                dealRepository.getDealById(dealId)?.let { deal ->
                    dealRepository.deleteDeal(deal)
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(error = e.message ?: "Failed to delete deal")
                }
            }
        }
    }

    private fun updateDealStage(dealId: Int, newStage: String) {
        viewModelScope.launch {
            try {
                dealRepository.updateDealStage(dealId, newStage)
            } catch (e: Exception) {
                _state.update { 
                    it.copy(error = e.message ?: "Failed to update deal stage")
                }
            }
        }
    }

    private fun getSortComparator(sortOrder: DealSortOrder): Comparator<DealWithInfo> {
        return when (sortOrder) {
            DealSortOrder.MODIFIED_TIME_DESC -> 
                compareByDescending { it.deal.modifiedTime }
            DealSortOrder.AMOUNT_ASC -> 
                compareBy { it.deal.amount }
            DealSortOrder.AMOUNT_DESC -> 
                compareByDescending { it.deal.amount }
            DealSortOrder.CLOSING_DATE_ASC -> 
                compareBy { it.deal.closingDate }
            DealSortOrder.CLOSING_DATE_DESC -> 
                compareByDescending { it.deal.closingDate }
        }
    }
} 