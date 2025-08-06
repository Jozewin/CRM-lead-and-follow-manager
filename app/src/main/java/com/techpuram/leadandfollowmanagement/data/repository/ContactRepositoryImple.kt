package com.techpuram.leadandfollowmanagement.data.repository

import com.techpuram.leadandfollowmanagement.data.local.dao.ContactDao
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow

class ContactRepositoryImple(
    private val contactDao: ContactDao
) : ContactRepository {

    // Insert a new contact
    override suspend fun insertContact(contact: Contact) : Long {
        return contactDao.insertContact(contact)
    }

    // Update an existing contact
    override suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    // Delete a contact
    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    // Get all contacts as Flow (for real-time updates)
    override fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
    }

    // Get a contact by ID
    override suspend fun getContactById(id: Int): Contact? {
        return contactDao.getContactById(id)
    }

    override suspend fun clearCustomFieldValue(columnName: String) {
        when (columnName) {
            "cf1" -> contactDao.clearCustomField1()
            "cf2" -> contactDao.clearCustomField2()
            "cf3" -> contactDao.clearCustomField3()
            "cf4" -> contactDao.clearCustomField4()
            "cf5" -> contactDao.clearCustomField5()
            "cf6" -> contactDao.clearCustomField6()
            "cf7" -> contactDao.clearCustomField7()
            "cf8" -> contactDao.clearCustomField8()
            "cf9" -> contactDao.clearCustomField9()
            "cf10" -> contactDao.clearCustomField10()
            "cf11" -> contactDao.clearCustomField11()
            "cf12" -> contactDao.clearCustomField12()
            "cf13" -> contactDao.clearCustomField13()
            "cf14" -> contactDao.clearCustomField14()
            "cf15" -> contactDao.clearCustomField15()
            "cf16" -> contactDao.clearCustomField16()
            "cf17" -> contactDao.clearCustomField17()
            "cf18" -> contactDao.clearCustomField18()
            "cf19" -> contactDao.clearCustomField19()
            "cf20" -> contactDao.clearCustomField20()
        }
    }
}
