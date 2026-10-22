package com.coppertrack.private.data.local.dao

import androidx.room.*
import com.coppertrack.private.data.local.entity.PaymentEntity
import com.coppertrack.private.domain.model.PaymentDirection
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE isReversal = 0 ORDER BY date DESC")
    fun getAllActive(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getById(id: Long): PaymentEntity?

    @Query("SELECT * FROM payments WHERE dealId = :dealId AND isReversal = 0 ORDER BY date DESC")
    fun getByDeal(dealId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE contactId = :contactId AND isReversal = 0 ORDER BY date DESC")
    fun getByContact(contactId: Long): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity): Long

    @Update
    suspend fun update(payment: PaymentEntity)

    @Query("""
        SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) 
        FROM payments 
        WHERE direction = :direction AND isReversal = 0
    """)
    suspend fun sumByDirection(direction: PaymentDirection): Double

    @Query("""
        SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) 
        FROM payments 
        WHERE dealId = :dealId AND direction = :direction AND isReversal = 0
    """)
    suspend fun sumByDealAndDirection(dealId: Long, direction: PaymentDirection): Double
}
