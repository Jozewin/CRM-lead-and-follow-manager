package com.techpuram.leadandfollowmanagement.domain.repository

import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import kotlinx.coroutines.flow.Flow

interface CustomFieldRepository {
    fun getAllCustomFields(): Flow<List<CustomField>>
    fun getCustomFieldsByModule(module: String): Flow<List<CustomField>>
    suspend fun insertCustomField(customField: CustomField): Long
    suspend fun deleteCustomField(customField: CustomField)
    suspend fun getNextAvailableColumnName(module: String): String

    suspend fun cleanupCustomFieldData(customField: CustomField)

} 