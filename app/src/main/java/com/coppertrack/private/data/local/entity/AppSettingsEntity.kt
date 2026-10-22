package com.coppertrack.private.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val businessName: String = "My Copper Business",
    val currency: String = "PKR",
    val weightUnit: String = "kg",
    val openingBalance: String = "0",
    val pinHash: String? = null,
    val biometricEnabled: Boolean = false,
    val lastBackupAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
