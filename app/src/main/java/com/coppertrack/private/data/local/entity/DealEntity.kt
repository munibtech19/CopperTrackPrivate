package com.coppertrack.private.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.coppertrack.private.domain.model.DealStatus
import com.coppertrack.private.domain.model.PaymentStatus

@Entity(
    tableName = "deals",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("supplierId"), Index("buyerId"), Index("dealDate"), Index("status")]
)
data class DealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val dealDate: Long,                         // epoch millis
    val supplierId: Long? = null,
    val buyerId: Long? = null,
    val copperType: String? = null,
    val copperGrade: String? = null,
    val description: String? = null,
    val notes: String? = null,

    // Quantities (stored as strings for precision)
    val purchasedQtyKg: String = "0",
    val receivedQtyKg: String = "0",
    val soldQtyKg: String = "0",
    val deliveredQtyKg: String = "0",

    // Rates
    val purchaseRatePerKg: String = "0",
    val sellingRatePerKg: String = "0",

    // Amounts (can be calculated or overridden)
    val purchaseAmount: String = "0",
    val salesAmount: String = "0",

    // Deal-level expenses
    val transportCost: String = "0",
    val loadingUnloadingCost: String = "0",
    val labourCost: String = "0",
    val weighingCost: String = "0",
    val packagingCost: String = "0",
    val commission: String = "0",
    val taxes: String = "0",
    val miscellaneousCost: String = "0",
    val discounts: String = "0",
    val expenseCredits: String = "0",

    // Status
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val status: DealStatus = DealStatus.DRAFT,

    // Flags
    val isCommissionOnly: Boolean = false,
    val isArchived: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
