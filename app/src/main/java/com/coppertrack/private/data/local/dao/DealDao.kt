package com.coppertrack.private.data.local.dao

import androidx.room.*
import com.coppertrack.private.data.local.entity.DealEntity
import com.coppertrack.private.domain.model.DealStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {
    @Query("SELECT * FROM deals WHERE isArchived = 0 ORDER BY dealDate DESC")
    fun getAllActive(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE id = :id")
    suspend fun getById(id: Long): DealEntity?

    @Query("SELECT * FROM deals WHERE isArchived = 0 AND status = :status ORDER BY dealDate DESC")
    fun getByStatus(status: DealStatus): Flow<List<DealEntity>>

    @Query("""
        SELECT * FROM deals 
        WHERE isArchived = 0 AND (
            title LIKE '%' || :query || '%' OR
            copperGrade LIKE '%' || :query || '%' OR
            description LIKE '%' || :query || '%'
        )
        ORDER BY dealDate DESC
    """)
    fun search(query: String): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE isArchived = 0 AND dealDate BETWEEN :from AND :to ORDER BY dealDate DESC")
    fun getByDateRange(from: Long, to: Long): Flow<List<DealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deal: DealEntity): Long

    @Update
    suspend fun update(deal: DealEntity)

    @Query("UPDATE deals SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archive(id: Long, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM deals WHERE isArchived = 0 AND status = :status")
    suspend fun countByStatus(status: DealStatus): Int

    @Query("SELECT COUNT(*) FROM deals WHERE isArchived = 0")
    suspend fun countActive(): Int
}
