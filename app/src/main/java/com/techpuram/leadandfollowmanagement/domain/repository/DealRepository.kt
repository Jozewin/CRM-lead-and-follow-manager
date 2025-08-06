package com.techpuram.leadandfollowmanagement.domain.repository

import com.techpuram.leadandfollowmanagement.domain.model.Deal
import kotlinx.coroutines.flow.Flow

interface DealRepository {
    fun getAllDeals(): Flow<List<Deal>>
    fun getDealsByStage(stage: String): Flow<List<Deal>>
    fun getDealsByContact(contactId: Int): Flow<List<Deal>>
    suspend fun getDealById(id: Int): Deal?
    suspend fun insertDeal(deal: Deal): Long
    suspend fun updateDeal(deal: Deal)
    suspend fun deleteDeal(deal: Deal)
    suspend fun updateDealStage(dealId: Int, newStage: String)
} 