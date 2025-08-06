package com.techpuram.leadandfollowmanagement.domain.repository

import com.techpuram.leadandfollowmanagement.domain.model.Lead
import kotlinx.coroutines.flow.Flow

interface LeadRepository {
    fun getAllLeads(): Flow<List<Lead>>
    suspend fun getLeadById(id: Int): Lead?
    suspend fun insertLead(lead: Lead): Long
    suspend fun updateLead(lead: Lead)
    suspend fun deleteLead(lead: Lead)
}