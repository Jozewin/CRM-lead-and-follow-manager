package com.techpuram.leadandfollowmanagement.domain.repository


import com.techpuram.leadandfollowmanagement.domain.model.FollowUp
import kotlinx.coroutines.flow.Flow

interface FollowUpRepository {
    suspend fun insertFollowUp(followUp: FollowUp): Long
    suspend fun updateFollowUp(followUp: FollowUp)
    suspend fun deleteFollowUp(followUp: FollowUp)
    suspend fun deleteFollowUpById(followUpId: Int)
    suspend fun getFollowUpById(id: Int): FollowUp?
    suspend fun updateFollowUpStage(followUpId: Int, newStage: String)
    fun getAllFollowUps(): Flow<List<FollowUp>>
    fun getFollowUpsByModule(module: String): Flow<List<FollowUp>>
    fun getPendingFollowUps(): Flow<List<FollowUp>>
    fun getFollowUpsByStage(stage: String): Flow<List<FollowUp>>
    fun getFollowUpsForRecord(recordId: Int, module: String): Flow<List<FollowUp>>
}
