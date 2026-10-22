package com.coppertrack.private

import android.app.Application
import com.coppertrack.private.data.local.db.AppDatabase
import com.coppertrack.private.data.repository.ContactRepository
import com.coppertrack.private.data.repository.DealRepository
import com.coppertrack.private.domain.usecase.DashboardSummaryUseCase

class CopperTrackApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }

    val dealRepository by lazy {
        DealRepository(database.dealDao(), database.inventoryDao())
    }
    val contactRepository by lazy {
        ContactRepository(database.contactDao())
    }
    val dashboardUseCase by lazy {
        DashboardSummaryUseCase(
            database.dealDao(),
            database.expenseDao(),
            database.paymentDao(),
            database.inventoryDao(),
            database.settingsDao()
        )
    }
}
