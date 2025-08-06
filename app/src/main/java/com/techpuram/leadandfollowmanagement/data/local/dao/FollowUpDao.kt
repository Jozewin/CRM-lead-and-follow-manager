package com.techpuram.leadandfollowmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.techpuram.leadandfollowmanagement.domain.model.FollowUp
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowUpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowUp(followUp: FollowUp): Long

    @Update
    suspend fun updateFollowUp(followUp: FollowUp)

    @Delete
    suspend fun deleteFollowUp(followUp: FollowUp)

    @Query("SELECT * FROM follow_up WHERE id = :id")
    suspend fun getFollowUpById(id: Int): FollowUp?

    @Query("SELECT * FROM follow_up ORDER BY dueTime DESC")
    fun getAllFollowUps(): Flow<List<FollowUp>>

    @Query("SELECT * FROM follow_up WHERE module = :module ORDER BY dueTime DESC")
    fun getFollowUpsByModule(module: String): Flow<List<FollowUp>>

    @Query("SELECT * FROM follow_up WHERE followUpStage != 'completed' ORDER BY dueTime ASC")
    fun getPendingFollowUps(): Flow<List<FollowUp>>

    // New queries for additional functionality
    @Query("DELETE FROM follow_up WHERE id = :followUpId")
    suspend fun deleteFollowUpById(followUpId: Int)

    @Query("UPDATE follow_up SET followUpStage = :newStage WHERE id = :followUpId")
    suspend fun updateFollowUpStage(followUpId: Int, newStage: String)

    @Query("SELECT * FROM follow_up WHERE followUpStage = :stage ORDER BY dueTime ASC")
    fun getFollowUpsByStage(stage: String): Flow<List<FollowUp>>

    @Query("SELECT * FROM follow_up WHERE recordId = :recordId AND module = :module")
    fun getFollowUpsForRecord(recordId: Int, module: String): Flow<List<FollowUp>>
}