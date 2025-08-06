package com.techpuram.leadandfollowmanagement.presentation.deal.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailDealViewModel @Inject constructor(
    private val dealRepository: DealRepository,
    private val contactRepository: ContactRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailDealState())
    val state: StateFlow<DetailDealState> = _state.asStateFlow()

    private val dealId: Int = savedStateHandle.get<Int>("dealId") ?: -1

    init {
        if (dealId != -1) {
            loadDeal(dealId)
        }
    }

    fun onEvent(event: DetailDealEvent) {
        when (event) {
            DetailDealEvent.DeleteDeal -> {
                deleteDeal()
            }
            DetailDealEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadDeal(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val deal = dealRepository.getDealById(id)
                deal?.let { safeDeal ->
                    val contact = safeDeal.contactId?.let { contactId ->
                        contactRepository.getContactById(contactId)
                    }
                    _state.update {
                        it.copy(
                            deal = safeDeal,
                            contactName = contact?.name ?:"as",
                            isLoading = false
                        )
                    }
                } ?: run {
                    _state.update {
                        it.copy(
                            error = "Deal not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to load deal",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun deleteDeal() {
        viewModelScope.launch {
            _state.value.deal?.let { deal ->
                try {
                    dealRepository.deleteDeal(deal)
                    _state.update { it.copy(deal = null) }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(error = e.message ?: "Failed to delete deal")
                    }
                }
            }
        }
    }
} 