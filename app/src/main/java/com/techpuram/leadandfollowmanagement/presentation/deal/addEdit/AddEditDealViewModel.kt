package com.techpuram.leadandfollowmanagement.presentation.deal.addEdit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.model.Deal
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditDealViewModel @Inject constructor(
    private val dealRepository: DealRepository,
    private val contactRepository: ContactRepository,
    customFieldRepository: CustomFieldRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditDealState())
    val state: StateFlow<AddEditDealState> = _state.asStateFlow()

    private val _customFields = MutableStateFlow<List<CustomField>>(emptyList())
    val customFields: StateFlow<List<CustomField>> = _customFields.asStateFlow()

    private val dealId: Int = savedStateHandle.get<Int>("dealId") ?: -1

    init {
        if (dealId != -1) {
            loadDeal(dealId)
        }

        viewModelScope.launch {
            customFieldRepository.getCustomFieldsByModule("Deal").collect{ fields ->
                _customFields.value = fields
            }
        }
    }

    fun onEvent(event: AddEditDealEvent) {
        when (event) {
            is AddEditDealEvent.TitleChanged -> {
                _state.update { it.copy(title = event.title) }
            }
            is AddEditDealEvent.AmountChanged -> {
                _state.update { it.copy(amount = event.amount) }
            }
            is AddEditDealEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            is AddEditDealEvent.StageChanged -> {
                _state.update { it.copy(stage = event.stage) }
            }
            is AddEditDealEvent.ProbabilityChanged -> {
                _state.update { it.copy(probability = event.probability) }
            }
            is AddEditDealEvent.ClosingDateChanged -> {
                _state.update { it.copy(closingDate = event.timestamp) }
            }
            is AddEditDealEvent.ContactSelected -> {
                _state.update {
                    it.copy(
                        contactId = event.id,
                        contactName = event.name
                    )
                }
            }
            AddEditDealEvent.SaveDeal -> {
                saveDeal()
            }
            AddEditDealEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
            is AddEditDealEvent.CustomFieldValueChanged -> {
                updateCustomField(event.fieldId, event.value)
            }


        }
    }

    private fun loadDeal(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                dealRepository.getDealById(id)?.let { deal ->
                    _state.update {
                        it.copy(
                            id = deal.id,
                            title = deal.title,
                            amount = deal.amount?.toString() ?: "",
                            description = deal.description ?: "",
                            stage = deal.stage,
                            probability = deal.probability?.toString() ?: "",
                            closingDate = deal.closingDate,
                            contactId = deal.contactId,
                            isLoading = false,
                            cf1 = deal.cf1,
                            cf2 = deal.cf2,
                            cf3 = deal.cf3,
                            cf4 = deal.cf4,
                            cf5 = deal.cf5,
                            cf6 = deal.cf6,
                            cf7 = deal.cf7,
                            cf8 = deal.cf8,
                            cf9 = deal.cf9,
                            cf10 = deal.cf10,
                            cf11 = deal.cf11,
                            cf12 = deal.cf12,
                            cf13 = deal.cf13,
                            cf14 = deal.cf14,
                            cf15 = deal.cf15,
                            cf16 = deal.cf16,
                            cf17 = deal.cf17,
                            cf18 = deal.cf18,
                            cf19 = deal.cf19,
                            cf20 = deal.cf20
                        )
                    }

                    // Load contact name if exists
                    deal.contactId?.let { contactId ->
                        contactRepository.getContactById(contactId)?.let { contact ->
                            _state.update { it.copy(contactName = contact.name) }
                        }
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

    private fun saveDeal() {
        val currentState = _state.value

        // Validate input
        if (currentState.title.isBlank()) {
            _state.update { it.copy(error = "Please enter a title") }
            return
        }

        if (currentState.contactId == null ) {
            _state.update { it.copy(error = "Please select a contact or lead") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val deal = Deal(
                    id = currentState.id ?: 0,
                    title = currentState.title,
                    amount = currentState.amount.toDoubleOrNull(),
                    description = currentState.description.takeIf { it.isNotBlank() },
                    stage = currentState.stage,
                    probability = currentState.probability.toIntOrNull(),
                    closingDate = currentState.closingDate,
                    contactId = currentState.contactId,
                    createdTime = System.currentTimeMillis(),
                    modifiedTime = System.currentTimeMillis(),
                    // Set other fields to null
                    cf1 = currentState.cf1,
                    cf2 = currentState.cf2,
                    cf3 = currentState.cf3,
                    cf4 = currentState.cf4,
                    cf5 = currentState.cf5,

                    cf6 = currentState.cf6,
                    cf7 = currentState.cf7,
                    cf8 = currentState.cf8,
                    cf9 = currentState.cf9,
                    cf10 = currentState.cf10,
                    cf11 = currentState.cf11,

                    cf12 = currentState.cf12,
                    cf13 = currentState.cf13,
                    cf14 = currentState.cf14,
                    cf15 = currentState.cf15,
                    cf16 = currentState.cf16,
                    cf17 = currentState.cf17,
                    cf18 = currentState.cf18,
                    cf19 = currentState.cf19,
                    cf20 = currentState.cf10,
                    prop = null
                )

                if (currentState.id == null) {
                    dealRepository.insertDeal(deal)
                } else {
                    dealRepository.updateDeal(deal)
                }

                _state.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = e.message ?: "Failed to save deal",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateCustomField(columnName: String, value: String) {
        _state.value = when (columnName) {
            "cf1" -> _state.value.copy(cf1 = value)
            "cf2" -> _state.value.copy(cf2 = value)
            "cf3" -> _state.value.copy(cf3 = value)
            "cf4" -> _state.value.copy(cf4 = value)
            "cf5" -> _state.value.copy(cf5 = value)
            "cf6" -> _state.value.copy(cf6 = value)
            "cf7" -> _state.value.copy(cf7 = value)
            "cf8" -> _state.value.copy(cf8 = value)
            "cf9" -> _state.value.copy(cf9 = value)
            "cf10" -> _state.value.copy(cf10 = value)
            "cf11" -> _state.value.copy(cf11 = value)
            "cf12" -> _state.value.copy(cf12 = value)
            "cf13" -> _state.value.copy(cf13 = value)
            "cf14" -> _state.value.copy(cf14 = value)
            "cf15" -> _state.value.copy(cf15 = value)
            "cf16" -> _state.value.copy(cf16 = value)
            "cf17" -> _state.value.copy(cf17 = value)
            "cf18" -> _state.value.copy(cf18 = value)
            "cf19" -> _state.value.copy(cf19 = value)
            "cf20" -> _state.value.copy(cf20 = value)
            else -> _state.value
        }
    }
} 