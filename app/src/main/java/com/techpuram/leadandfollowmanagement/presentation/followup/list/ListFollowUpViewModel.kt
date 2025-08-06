package com.techpuram.leadandfollowmanagement.presentation.followup.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.repository.FollowUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.techpuram.leadandfollowmanagement.domain.model.FollowUp

@HiltViewModel
class ListFollowUpViewModel @Inject constructor(
    private val followUpRepository: FollowUpRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FollowUpListState())
    val state: StateFlow<FollowUpListState> = _state.asStateFlow()

    init {
        loadFollowUps()
    }

    fun onEvent(event: ListFollowUpEvent) {
        when (event) {
            is ListFollowUpEvent.FilterChanged -> {
                _state.update { it.copy(filterType = event.filterType) }
                loadFollowUps()
            }

            is ListFollowUpEvent.SortOrderChanged -> {
                _state.update { it.copy(sortOrder = event.sortOrder) }
                loadFollowUps()
            }

            is ListFollowUpEvent.DeleteFollowUp -> {
                deleteFollowUp(event.followUpId)
            }

            is ListFollowUpEvent.UpdateFollowUpStage -> {
                updateFollowUpStage(event.followUpId, event.newStage)
            }

            ListFollowUpEvent.RefreshFollowUps -> {
                loadFollowUps()
            }

            ListFollowUpEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadFollowUps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                followUpRepository.getAllFollowUps().collect { followUps ->
                    val filteredAndSortedFollowUps = followUps
                        .filter { followUp ->
                            when (_state.value.filterType) {
                                "All" -> true
                                "Scheduled" -> followUp.followUpStage == "scheduled"
                                "In Progress" -> followUp.followUpStage == "in-progress"
                                "Completed" -> followUp.followUpStage == "completed"
                                else -> true
                            }
                        }
                        .sortedWith(getSortComparator(_state.value.sortOrder))

                    _state.update {
                        it.copy(
                            isLoading = false,
                            followUps = filteredAndSortedFollowUps
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load follow-ups"
                    )
                }
            }
        }
    }

    private fun deleteFollowUp(followUpId: Int) {
        viewModelScope.launch {
            try {
                followUpRepository.deleteFollowUpById(followUpId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to delete follow-up") }
            }
        }
    }

    private fun updateFollowUpStage(followUpId: Int, newStage: String) {
        viewModelScope.launch {
            try {
                followUpRepository.updateFollowUpStage(followUpId, newStage)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to update follow-up stage") }
            }
        }
    }

    private fun getSortComparator(sortOrder: SortOrder): Comparator<FollowUp> {
        return when (sortOrder) {
            SortOrder.DUE_DATE_ASC -> compareBy { it.dueTime }
            SortOrder.DUE_DATE_DESC -> compareByDescending { it.dueTime }
            SortOrder.PRIORITY_ASC -> compareBy { it.priority }
            SortOrder.PRIORITY_DESC -> compareByDescending { it.priority }
        }
    }
}