package com.coppertrack.private.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coppertrack.private.data.local.dao.*
import com.coppertrack.private.data.local.entity.*

@Database(
    entities = [
        ContactEntity::class,
        DealEntity::class,
        ExpenseEntity::class,
        PaymentEntity::class,
        InventoryEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun dealDao(): DealDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun paymentDao(): PaymentDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coppertrack_private.db"
                )
                    .fallbackToDestructiveMigration() // for v1 only; add proper migrations later
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
