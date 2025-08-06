package com.techpuram.leadandfollowmanagement.data.local

import androidx.room.*
import com.techpuram.leadandfollowmanagement.domain.model.Lead
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM lead ORDER BY name ASC")
    fun getAllLeads(): Flow<List<Lead>>

    @Query("SELECT * FROM lead WHERE id = :id")
    suspend fun getLeadById(id: Int): Lead?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: Lead): Long

    @Update
    suspend fun updateLead(lead: Lead)

    @Delete
    suspend fun deleteLead(lead: Lead)


    // Clear custom field methods for each column
    @Query("UPDATE lead SET cf1 = NULL WHERE cf1 IS NOT NULL")
    suspend fun clearCustomField1(): Int

    @Query("UPDATE lead SET cf2 = NULL WHERE cf2 IS NOT NULL")
    suspend fun clearCustomField2(): Int

    @Query("UPDATE lead SET cf3 = NULL WHERE cf3 IS NOT NULL")
    suspend fun clearCustomField3(): Int

    @Query("UPDATE lead SET cf4 = NULL WHERE cf4 IS NOT NULL")
    suspend fun clearCustomField4(): Int

    @Query("UPDATE lead SET cf5 = NULL WHERE cf5 IS NOT NULL")
    suspend fun clearCustomField5(): Int

    @Query("UPDATE lead SET cf6 = NULL WHERE cf6 IS NOT NULL")
    suspend fun clearCustomField6(): Int

    @Query("UPDATE lead SET cf7 = NULL WHERE cf7 IS NOT NULL")
    suspend fun clearCustomField7(): Int

    @Query("UPDATE lead SET cf8 = NULL WHERE cf8 IS NOT NULL")
    suspend fun clearCustomField8(): Int

    @Query("UPDATE lead SET cf9 = NULL WHERE cf9 IS NOT NULL")
    suspend fun clearCustomField9(): Int

    @Query("UPDATE lead SET cf10 = NULL WHERE cf10 IS NOT NULL")
    suspend fun clearCustomField10(): Int

    @Query("UPDATE lead SET cf11 = NULL WHERE cf11 IS NOT NULL")
    suspend fun clearCustomField11(): Int

    @Query("UPDATE lead SET cf12 = NULL WHERE cf12 IS NOT NULL")
    suspend fun clearCustomField12(): Int

    @Query("UPDATE lead SET cf13 = NULL WHERE cf13 IS NOT NULL")
    suspend fun clearCustomField13(): Int

    @Query("UPDATE lead SET cf14 = NULL WHERE cf14 IS NOT NULL")
    suspend fun clearCustomField14(): Int

    @Query("UPDATE lead SET cf15 = NULL WHERE cf15 IS NOT NULL")
    suspend fun clearCustomField15(): Int

    @Query("UPDATE lead SET cf16 = NULL WHERE cf16 IS NOT NULL")
    suspend fun clearCustomField16(): Int

    @Query("UPDATE lead SET cf17 = NULL WHERE cf17 IS NOT NULL")
    suspend fun clearCustomField17(): Int

    @Query("UPDATE lead SET cf18 = NULL WHERE cf18 IS NOT NULL")
    suspend fun clearCustomField18(): Int

    @Query("UPDATE lead SET cf19 = NULL WHERE cf19 IS NOT NULL")
    suspend fun clearCustomField19(): Int

    @Query("UPDATE lead SET cf20 = NULL WHERE cf20 IS NOT NULL")
    suspend fun clearCustomField20(): Int
}