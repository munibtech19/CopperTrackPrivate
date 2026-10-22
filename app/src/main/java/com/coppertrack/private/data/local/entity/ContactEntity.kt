package com.coppertrack.private.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coppertrack.private.domain.model.ContactType

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val businessName: String? = null,
    val city: String? = null,
    val location: String? = null,
    val type: ContactType,
    val notes: String? = null,
    val copperGrades: String? = null,          // comma-separated
    val quotedPurchaseRate: String? = null,    // stored as string for BigDecimal safety
    val quotedSellingRate: String? = null,
    val lastContactDate: Long? = null,         // epoch millis
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
