package com.coppertrack.private.data.local.dao

import androidx.room.*
import com.coppertrack.private.data.local.entity.InventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory WHERE isClosed = 0 ORDER BY purchaseDate DESC")
    fun getOpenStock(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getById(id: Long): InventoryEntity?

    @Query("SELECT * FROM inventory WHERE sourceDealId = :dealId")
    suspend fun getByDeal(dealId: Long): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryEntity): Long

    @Update
    suspend fun update(item: InventoryEntity)

    @Query("SELECT COALESCE(SUM(CAST(remainingQtyKg AS REAL)), 0) FROM inventory WHERE isClosed = 0")
    suspend fun totalRemainingKg(): Double

    @Query("SELECT COALESCE(SUM(CAST(inventoryValue AS REAL)), 0) FROM inventory WHERE isClosed = 0")
    suspend fun totalInventoryValue(): Double
}
