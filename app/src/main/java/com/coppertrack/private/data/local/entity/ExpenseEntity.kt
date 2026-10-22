package com.coppertrack.private.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.coppertrack.private.domain.model.ExpenseCategory
import com.coppertrack.private.domain.model.ExpenseStatus
import com.coppertrack.private.domain.model.PaymentMethod

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = DealEntity::class,
            parentColumns = ["id"],
            childColumns = ["dealId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("dealId"), Index("date"), Index("category")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val category: ExpenseCategory,
    val amount: String,                         // BigDecimal as string
    val dealId: Long? = null,                   // null = general overhead
    val paidBy: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val payee: String? = null,
    val receiptPath: String? = null,            // local file path
    val notes: String? = null,
    val status: ExpenseStatus = ExpenseStatus.PAID,
    val isLinkedToDealExpense: Boolean = false, // true if already counted inside deal
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
