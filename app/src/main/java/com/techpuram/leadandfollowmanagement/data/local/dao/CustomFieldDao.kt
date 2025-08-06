package com.techpuram.leadandfollowmanagement.data.local.dao

import androidx.room.*
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomFieldDao {
    @Query("SELECT * FROM custom_field ORDER BY createdTime DESC")
    fun getAllCustomFields(): Flow<List<CustomField>>

    @Query("SELECT * FROM custom_field WHERE module = :module ORDER BY columnName ASC")
    fun getCustomFieldsByModule(module: String): Flow<List<CustomField>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomField(customField: CustomField): Long

    @Delete
    suspend fun deleteCustomField(customField: CustomField)

    @Query("SELECT COUNT(*) FROM custom_field WHERE module = :module")
    suspend fun getCustomFieldCountByModule(module: String): Int

    @Query("SELECT MIN(CAST(SUBSTR(columnName, 3) AS INTEGER)) FROM custom_field WHERE module = :module AND SUBSTR(columnName, 3) NOT IN (SELECT SUBSTR(columnName, 3) FROM custom_field WHERE module = :module)")
    suspend fun getFirstAvailableColumnNumber(module: String): Int?
}
