package com.techpuram.leadandfollowmanagement.data.repository

import com.techpuram.leadandfollowmanagement.data.local.dao.DealDao
import com.techpuram.leadandfollowmanagement.domain.model.Deal
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DealRepositoryImpl @Inject constructor(
    private val dealDao: DealDao
) : DealRepository {
    
    override fun getAllDeals(): Flow<List<Deal>> = dealDao.getAllDeals()
    
    override fun getDealsByStage(stage: String): Flow<List<Deal>> = 
        dealDao.getDealsByStage(stage)
    
    override fun getDealsByContact(contactId: Int): Flow<List<Deal>> = 
        dealDao.getDealsByContact(contactId)
    override suspend fun getDealById(id: Int): Deal? = dealDao.getDealById(id)
    
    override suspend fun insertDeal(deal: Deal): Long = dealDao.insertDeal(deal)
    
    override suspend fun updateDeal(deal: Deal) = dealDao.updateDeal(deal)
    
    override suspend fun deleteDeal(deal: Deal) = dealDao.deleteDeal(deal)
    
    override suspend fun updateDealStage(dealId: Int, newStage: String) {
        dealDao.updateDealStage(dealId, newStage)
    }
} 