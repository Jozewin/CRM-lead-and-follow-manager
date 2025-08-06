package com.techpuram.leadandfollowmanagement.data.repository

import com.techpuram.leadandfollowmanagement.data.local.LeadDao
import com.techpuram.leadandfollowmanagement.data.local.dao.ContactDao
import com.techpuram.leadandfollowmanagement.data.local.dao.CustomFieldDao
import com.techpuram.leadandfollowmanagement.data.local.dao.DealDao
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CustomFieldRepositoryImpl(
    private val customFieldDao: CustomFieldDao,
    private val contactDao: ContactDao,
    private val leadDao: LeadDao,
    private val dealDao: DealDao
) : CustomFieldRepository {

    override fun getAllCustomFields(): Flow<List<CustomField>> {
        return customFieldDao.getAllCustomFields()
    }

    override fun getCustomFieldsByModule(module: String): Flow<List<CustomField>> {
        return customFieldDao.getCustomFieldsByModule(module)
    }

    override suspend fun insertCustomField(customField: CustomField): Long {
        return customFieldDao.insertCustomField(customField)
    }

    override suspend fun deleteCustomField(customField: CustomField) {
        customFieldDao.deleteCustomField(customField)
    }

    override suspend fun getNextAvailableColumnName(module: String): String {
        // Get all existing custom fields for this module
        val existingFields = customFieldDao.getCustomFieldsByModule(module).first()
        val usedColumnNames = existingFields.map { it.columnName }

        // Find the first available slot from 1 to 20
        for (i in 1..20) {
            val columnName = "cf$i"
            if (columnName !in usedColumnNames) {
                return columnName
            }
        }

        // If all slots are used, throw an exception
        throw IllegalStateException("Maximum number of custom fields (20) reached for module $module")
    }

    override suspend fun cleanupCustomFieldData(customField: CustomField) {
        // First delete the custom field definition
        customFieldDao.deleteCustomField(customField)

        // Then clean up the data in the corresponding module
        when (customField.module) {
            "Contact" -> cleanupContactCustomFieldData(customField.columnName)
            "Lead" -> cleanupLeadCustomFieldData(customField.columnName)
            "Deal" -> cleanupDealCustomFieldData(customField.columnName)
        }
    }

    private suspend fun cleanupDealCustomFieldData(columnName: String) {
        val deals = dealDao.getAllDeals().first()

        deals.forEach{ lead ->
            val updatedDeal = when (columnName) {
                "cf1" -> lead.copy(cf1 = null)
                "cf2" -> lead.copy(cf2 = null)
                "cf3" -> lead.copy(cf3 = null)
                "cf4" -> lead.copy(cf4 = null)
                "cf5" -> lead.copy(cf5 = null)
                "cf6" -> lead.copy(cf6 = null)
                "cf7" -> lead.copy(cf7 = null)
                "cf8" -> lead.copy(cf8 = null)
                "cf9" -> lead.copy(cf9 = null)
                "cf10" -> lead.copy(cf10 = null)
                "cf11" -> lead.copy(cf11 = null)
                "cf12" -> lead.copy(cf12 = null)
                "cf13" -> lead.copy(cf13 = null)
                "cf14" -> lead.copy(cf14 = null)
                "cf15" -> lead.copy(cf15 = null)
                "cf16" -> lead.copy(cf16 = null)
                "cf17" -> lead.copy(cf17 = null)
                "cf18" -> lead.copy(cf18 = null)
                "cf19" -> lead.copy(cf19 = null)
                "cf20" -> lead.copy(cf20 = null)
                else -> lead
            }
            if(updatedDeal != lead){
                dealDao.updateDeal(updatedDeal)
            }
        }
    }

    private suspend fun cleanupLeadCustomFieldData(columnName: String) {
        // Get all leads
        val leads = leadDao.getAllLeads().first()

        leads.forEach{ lead ->
            val updatedLead = when (columnName) {
                "cf1" -> lead.copy(cf1 = null)
                "cf2" -> lead.copy(cf2 = null)
                "cf3" -> lead.copy(cf3 = null)
                "cf4" -> lead.copy(cf4 = null)
                "cf5" -> lead.copy(cf5 = null)
                "cf6" -> lead.copy(cf6 = null)
                "cf7" -> lead.copy(cf7 = null)
                "cf8" -> lead.copy(cf8 = null)
                "cf9" -> lead.copy(cf9 = null)
                "cf10" -> lead.copy(cf10 = null)
                "cf11" -> lead.copy(cf11 = null)
                "cf12" -> lead.copy(cf12 = null)
                "cf13" -> lead.copy(cf13 = null)
                "cf14" -> lead.copy(cf14 = null)
                "cf15" -> lead.copy(cf15 = null)
                "cf16" -> lead.copy(cf16 = null)
                "cf17" -> lead.copy(cf17 = null)
                "cf18" -> lead.copy(cf18 = null)
                "cf19" -> lead.copy(cf19 = null)
                "cf20" -> lead.copy(cf20 = null)
                else -> lead
            }
            if(updatedLead != lead){
                leadDao.updateLead(updatedLead)
            }
        }
    }
    private suspend fun cleanupContactCustomFieldData(columnName: String) {
        // Get all contacts
        val contacts = contactDao.getAllContacts().first()

        // Update each contact to clear the custom field value
        contacts.forEach { contact ->
            val updatedContact = when (columnName) {
                "cf1" -> contact.copy(cf1 = null)
                "cf2" -> contact.copy(cf2 = null)
                "cf3" -> contact.copy(cf3 = null)
                "cf4" -> contact.copy(cf4 = null)
                "cf5" -> contact.copy(cf5 = null)
                "cf6" -> contact.copy(cf6 = null)
                "cf7" -> contact.copy(cf7 = null)
                "cf8" -> contact.copy(cf8 = null)
                "cf9" -> contact.copy(cf9 = null)
                "cf10" -> contact.copy(cf10 = null)
                "cf11" -> contact.copy(cf11 = null)
                "cf12" -> contact.copy(cf12 = null)
                "cf13" -> contact.copy(cf13 = null)
                "cf14" -> contact.copy(cf14 = null)
                "cf15" -> contact.copy(cf15 = null)
                "cf16" -> contact.copy(cf16 = null)
                "cf17" -> contact.copy(cf17 = null)
                "cf18" -> contact.copy(cf18 = null)
                "cf19" -> contact.copy(cf19 = null)
                "cf20" -> contact.copy(cf20 = null)
                else -> contact
            }

            if (updatedContact != contact) {
                contactDao.updateContact(updatedContact)
            }
        }
    }
} 