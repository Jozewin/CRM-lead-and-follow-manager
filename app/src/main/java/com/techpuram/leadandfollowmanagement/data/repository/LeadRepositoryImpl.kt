package com.techpuram.leadandfollowmanagement.data.repository

import com.techpuram.leadandfollowmanagement.data.local.LeadDao
import com.techpuram.leadandfollowmanagement.domain.model.Lead
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LeadRepositoryImpl(
    private val leadDao: LeadDao
) : LeadRepository {

    override fun getAllLeads(): Flow<List<Lead>> {
        return leadDao.getAllLeads()
    }

    override suspend fun getLeadById(id: Int): Lead? {
        return leadDao.getLeadById(id)
    }

    override suspend fun insertLead(lead: Lead): Long {
        return leadDao.insertLead(lead)
    }

    override suspend fun updateLead(lead: Lead) {
        leadDao.updateLead(lead)
    }

    override suspend fun deleteLead(lead: Lead) {
        leadDao.deleteLead(lead)
    }

}