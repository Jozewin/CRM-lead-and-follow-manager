package com.techpuram.leadandfollowmanagement.presentation.followup.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.repository.FollowUpRepository
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailFollowUpViewModel @Inject constructor(
    private val followUpRepository: FollowUpRepository,
    private val contactRepository: ContactRepository,
    private val leadRepository: LeadRepository,
    private val dealRepository: DealRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailFollowUpState())
    val state: StateFlow<DetailFollowUpState> = _state.asStateFlow()

    init {
        savedStateHandle.get<Int>("followUpId")?.let { id ->
            if (id != -1) {
                loadFollowUp(id)
            }
        }
    }

    private fun loadFollowUp(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val followUp = followUpRepository.getFollowUpById(id)
                if (followUp != null) {
                    _state.update { 
                        it.copy(
                            followUp = followUp,
                            isLoading = false
                        )
                    }
                    
                    try {
                        val recordName = when (followUp.module) {
                            "Contact" -> {
                                val contact = contactRepository.getContactById(followUp.recordId)
                                contact?.name ?: "Unknown Contact"
                            }
                            "Lead" -> {
                                val lead = leadRepository.getLeadById(followUp.recordId)
                                lead?.name ?: "Unknown Lead"
                            }
                            "Deal" -> {
                                val deal = dealRepository.getDealById(followUp.recordId)
                                val contact = contactRepository.getContactById(deal?.contactId!!)
                                contact?.name ?: "Unknown Contact"
                            }
                            else -> "Unknown Record"
                        }
                        
                        _state.update { 
                            it.copy(recordName = recordName)
                        }
                    } catch (e: Exception) {
                        _state.update { 
                            it.copy(recordName = "Error loading record name")
                        }
                    }
                } else {
                    _state.update { 
                        it.copy(
                            error = "Follow-up not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = e.message ?: "An error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun deleteFollowUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                state.value.followUp?.let { followUp ->
                    followUpRepository.deleteFollowUp(followUp)
                    _state.update { it.copy(isDeleted = true) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to delete follow-up",
                        isLoading = false
                    )
                }
            }
        }
    }
} 