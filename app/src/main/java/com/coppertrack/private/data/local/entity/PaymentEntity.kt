package com.coppertrack.private.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.coppertrack.private.domain.model.PaymentDirection
import com.coppertrack.private.domain.model.PaymentMethod

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = DealEntity::class,
            parentColumns = ["id"],
            childColumns = ["dealId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("dealId"), Index("contactId"), Index("date"), Index("direction")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val amount: String,
    val direction: PaymentDirection,
    val method: PaymentMethod = PaymentMethod.CASH,
    val dealId: Long? = null,
    val contactId: Long? = null,
    val reference: String? = null,              // cheque no, transfer ref, etc.
    val notes: String? = null,
    val isAdvance: Boolean = false,
    val isReversal: Boolean = false,
    val reversedPaymentId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
