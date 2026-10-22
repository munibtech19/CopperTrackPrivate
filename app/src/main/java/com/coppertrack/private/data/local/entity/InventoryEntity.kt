package com.coppertrack.private.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory",
    foreignKeys = [
        ForeignKey(
            entity = DealEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceDealId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("sourceDealId"), Index("copperGrade")]
)
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val copperCategory: String? = null,
    val copperGrade: String,
    val sourceDealId: Long? = null,
    val purchaseDate: Long,
    val purchasedQtyKg: String,
    val receivedQtyKg: String,
    val soldQtyKg: String = "0",
    val remainingQtyKg: String,
    val purchaseCost: String,
    val inventoryValue: String,                 // value of remaining
    val allocatedTransport: String = "0",
    val allocatedHandling: String = "0",
    val location: String? = null,
    val notes: String? = null,
    val photoPath: String? = null,
    val weightDiscrepancyKg: String = "0",
    val discrepancyReason: String? = null,
    val isClosed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
