package com.techpuram.leadandfollowmanagement.presentation.lead.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.Lead
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListLeadViewModel @Inject constructor(
    private val repository: LeadRepository
) : ViewModel() {

    private val _allLeads = MutableStateFlow<List<Lead>>(emptyList())
    private val _selectedStatus = MutableStateFlow<String?>(null)

    val leads: StateFlow<List<Lead>> = combine(_allLeads, _selectedStatus) { allLeads, selectedStatus ->
        if (selectedStatus == null) {
            allLeads
        } else {
            allLeads.filter { it.status.equals(selectedStatus, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), emptyList())


    val selectedStatus: StateFlow<String?> = _selectedStatus

    init {
        fetchLeads()
    }

    private fun fetchLeads() {
        viewModelScope.launch {
            repository.getAllLeads().collect { leadList ->
                _allLeads.value = leadList
            }
        }
    }

    fun filterByStatus(status: String?) {
        _selectedStatus.value = status
    }


}