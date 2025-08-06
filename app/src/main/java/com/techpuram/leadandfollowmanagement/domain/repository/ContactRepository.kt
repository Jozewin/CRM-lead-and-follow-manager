package com.techpuram.leadandfollowmanagement.domain.repository

import com.techpuram.leadandfollowmanagement.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    suspend fun insertContact(contact: Contact) : Long

    suspend fun updateContact(contact: Contact)

    suspend fun deleteContact(contact: Contact)

    fun getAllContacts(): Flow<List<Contact>>

    suspend fun getContactById(id: Int): Contact?

    suspend fun clearCustomFieldValue(columnName: String)
}