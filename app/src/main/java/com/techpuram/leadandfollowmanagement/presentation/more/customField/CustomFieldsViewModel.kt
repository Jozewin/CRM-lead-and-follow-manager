package com.techpuram.leadandfollowmanagement.presentation.more.customField

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomFieldsViewModel @Inject constructor(
    private val customFieldRepository: CustomFieldRepository
) : ViewModel() {

    val customFields: StateFlow<List<CustomField>> = customFieldRepository
        .getAllCustomFields()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedModule = MutableStateFlow("Contact")
    val selectedModule: StateFlow<String> = _selectedModule

    fun setSelectedModule(module: String) {
        _selectedModule.value = module
    }

    fun createCustomField(fieldName: String, fieldType: String) {
        if (fieldName.isBlank()) return
        
        viewModelScope.launch {
            try {
                val columnName = customFieldRepository.getNextAvailableColumnName(_selectedModule.value)
                val customField = CustomField(
                    module = _selectedModule.value,
                    fieldName = fieldName,
                    fieldType = fieldType,
                    columnName = columnName
                )
                customFieldRepository.insertCustomField(customField)
            } catch (e: Exception) {
                // Handle error (e.g., maximum custom fields reached)
            }
        }
    }

    fun deleteCustomField(customField: CustomField) {
        viewModelScope.launch {
            customFieldRepository.cleanupCustomFieldData(customField)
        }
    }
} 