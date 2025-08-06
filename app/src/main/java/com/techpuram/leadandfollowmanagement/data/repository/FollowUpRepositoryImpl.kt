package com.techpuram.leadandfollowmanagement.data.repository

import com.techpuram.leadandfollowmanagement.data.local.dao.FollowUpDao
import com.techpuram.leadandfollowmanagement.domain.model.FollowUp
import com.techpuram.leadandfollowmanagement.domain.repository.FollowUpRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FollowUpRepositoryImpl @Inject constructor(
    private val followUpDao: FollowUpDao
) : FollowUpRepository {

    override suspend fun insertFollowUp(followUp: FollowUp): Long {
        return followUpDao.insertFollowUp(followUp.toEntity())
    }

    override suspend fun updateFollowUp(followUp: FollowUp) {
        followUpDao.updateFollowUp(followUp.toEntity())
    }

    override suspend fun deleteFollowUp(followUp: FollowUp) {
        followUpDao.deleteFollowUp(followUp.toEntity())
    }

    override suspend fun deleteFollowUpById(followUpId: Int) {
        followUpDao.deleteFollowUpById(followUpId)
    }

    override suspend fun getFollowUpById(id: Int): FollowUp? {
        return followUpDao.getFollowUpById(id)?.toDomain()
    }

    override suspend fun updateFollowUpStage(followUpId: Int, newStage: String) {
        followUpDao.updateFollowUpStage(followUpId, newStage)
    }

    override fun getAllFollowUps(): Flow<List<FollowUp>> {
        return followUpDao.getAllFollowUps().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFollowUpsByModule(module: String): Flow<List<FollowUp>> {
        return followUpDao.getFollowUpsByModule(module).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPendingFollowUps(): Flow<List<FollowUp>> {
        return followUpDao.getPendingFollowUps().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFollowUpsByStage(stage: String): Flow<List<FollowUp>> {
        return followUpDao.getFollowUpsByStage(stage)
    }

    override fun getFollowUpsForRecord(recordId: Int, module: String): Flow<List<FollowUp>> {
        return followUpDao.getFollowUpsForRecord(recordId, module)
    }

    private fun FollowUp.toEntity(): FollowUp {
        return FollowUp(
            id = id,
            module = module,
            dueTime = dueTime,
            notes = notes,
            followUpStage = followUpStage,
            followUpType = followUpType,
            priority = priority,
            reminder = reminder,
            parentFollowup = parentFollowup,
            createdTime = createdTime,
            recordId = recordId,
            prop = prop
        )
    }

    private fun FollowUp.toDomain(): FollowUp {
        return FollowUp(
            id = id,
            module = module,
            dueTime = dueTime,
            notes = notes,
            followUpStage = followUpStage,
            followUpType = followUpType,
            priority = priority,
            reminder = reminder,
            parentFollowup = parentFollowup,
            createdTime = createdTime,
            recordId = recordId,
            prop = prop
        )
    }
}
