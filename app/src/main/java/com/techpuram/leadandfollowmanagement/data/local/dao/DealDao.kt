package com.techpuram.leadandfollowmanagement.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
import com.techpuram.leadandfollowmanagement.domain.model.Deal

@Dao
interface DealDao {
    @Query("SELECT * FROM deal ORDER BY modifiedTime DESC")
    fun getAllDeals(): Flow<List<Deal>>

    @Query("SELECT * FROM deal WHERE stage = :stage ORDER BY modifiedTime DESC")
    fun getDealsByStage(stage: String): Flow<List<Deal>>

    @Query("UPDATE deal SET stage = :newStage, modifiedTime = :timestamp WHERE id = :dealId")
    suspend fun updateDealStage(dealId: Int, newStage: String, timestamp: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: Deal): Long

    @Update
    suspend fun updateDeal(deal: Deal)

    @Delete
    suspend fun deleteDeal(deal: Deal)

    @Query("SELECT * FROM deal WHERE id = :id")
    suspend fun getDealById(id: Int): Deal?

    @Query("SELECT * FROM deal WHERE contactId = :contactId")
    fun getDealsByContact(contactId: Int): Flow<List<Deal>>
}
