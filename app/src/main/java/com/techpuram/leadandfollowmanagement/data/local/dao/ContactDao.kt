package com.techpuram.leadandfollowmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.*
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contact ORDER BY createdTime DESC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE id = :contactId LIMIT 1")
    suspend fun getContactById(contactId: Int): Contact?

    @Query("SELECT * FROM contact WHERE name LIKE '%' || :query || '%' OR companyName LIKE '%' || :query || '%' ORDER BY createdTime DESC")
    fun searchContacts(query: String): Flow<List<Contact>>

    // Clear custom field methods for each column
    @Query("UPDATE contact SET cf1 = NULL WHERE cf1 IS NOT NULL")
    suspend fun clearCustomField1(): Int

    @Query("UPDATE contact SET cf2 = NULL WHERE cf2 IS NOT NULL")
    suspend fun clearCustomField2(): Int

    @Query("UPDATE contact SET cf3 = NULL WHERE cf3 IS NOT NULL")
    suspend fun clearCustomField3(): Int

    @Query("UPDATE contact SET cf4 = NULL WHERE cf4 IS NOT NULL")
    suspend fun clearCustomField4(): Int

    @Query("UPDATE contact SET cf5 = NULL WHERE cf5 IS NOT NULL")
    suspend fun clearCustomField5(): Int

    @Query("UPDATE contact SET cf6 = NULL WHERE cf6 IS NOT NULL")
    suspend fun clearCustomField6(): Int

    @Query("UPDATE contact SET cf7 = NULL WHERE cf7 IS NOT NULL")
    suspend fun clearCustomField7(): Int

    @Query("UPDATE contact SET cf8 = NULL WHERE cf8 IS NOT NULL")
    suspend fun clearCustomField8(): Int

    @Query("UPDATE contact SET cf9 = NULL WHERE cf9 IS NOT NULL")
    suspend fun clearCustomField9(): Int

    @Query("UPDATE contact SET cf10 = NULL WHERE cf10 IS NOT NULL")
    suspend fun clearCustomField10(): Int

    @Query("UPDATE contact SET cf11 = NULL WHERE cf11 IS NOT NULL")
    suspend fun clearCustomField11(): Int

    @Query("UPDATE contact SET cf12 = NULL WHERE cf12 IS NOT NULL")
    suspend fun clearCustomField12(): Int

    @Query("UPDATE contact SET cf13 = NULL WHERE cf13 IS NOT NULL")
    suspend fun clearCustomField13(): Int

    @Query("UPDATE contact SET cf14 = NULL WHERE cf14 IS NOT NULL")
    suspend fun clearCustomField14(): Int

    @Query("UPDATE contact SET cf15 = NULL WHERE cf15 IS NOT NULL")
    suspend fun clearCustomField15(): Int

    @Query("UPDATE contact SET cf16 = NULL WHERE cf16 IS NOT NULL")
    suspend fun clearCustomField16(): Int

    @Query("UPDATE contact SET cf17 = NULL WHERE cf17 IS NOT NULL")
    suspend fun clearCustomField17(): Int

    @Query("UPDATE contact SET cf18 = NULL WHERE cf18 IS NOT NULL")
    suspend fun clearCustomField18(): Int

    @Query("UPDATE contact SET cf19 = NULL WHERE cf19 IS NOT NULL")
    suspend fun clearCustomField19(): Int

    @Query("UPDATE contact SET cf20 = NULL WHERE cf20 IS NOT NULL")
    suspend fun clearCustomField20(): Int
}
