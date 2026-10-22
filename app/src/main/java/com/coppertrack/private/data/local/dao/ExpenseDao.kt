package com.coppertrack.private.data.local.dao

import androidx.room.*
import com.coppertrack.private.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE dealId = :dealId ORDER BY date DESC")
    fun getByDeal(dealId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun getByDateRange(from: Long, to: Long): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) FROM expenses WHERE isLinkedToDealExpense = 0")
    suspend fun totalUnlinkedExpenses(): Double
}
