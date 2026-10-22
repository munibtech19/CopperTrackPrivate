package com.coppertrack.private.data.repository

import com.coppertrack.private.data.local.dao.DealDao
import com.coppertrack.private.data.local.dao.InventoryDao
import com.coppertrack.private.data.local.entity.DealEntity
import com.coppertrack.private.data.local.entity.InventoryEntity
import com.coppertrack.private.domain.calculator.ProfitCalculator
import com.coppertrack.private.domain.model.DealStatus
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

class DealRepository(
    private val dealDao: DealDao,
    private val inventoryDao: InventoryDao
) {
    fun getAllActive(): Flow<List<DealEntity>> = dealDao.getAllActive()
    fun getByStatus(status: DealStatus): Flow<List<DealEntity>> = dealDao.getByStatus(status)
    fun search(query: String): Flow<List<DealEntity>> = dealDao.search(query)
    fun getByDateRange(from: Long, to: Long): Flow<List<DealEntity>> = dealDao.getByDateRange(from, to)

    suspend fun getById(id: Long): DealEntity? = dealDao.getById(id)

    suspend fun save(deal: DealEntity): Long {
        val id = if (deal.id == 0L) dealDao.insert(deal) else {
            dealDao.update(deal.copy(updatedAt = System.currentTimeMillis()))
            deal.id
        }
        // Sync inventory when purchased/received
        syncInventory(deal.copy(id = id))
        return id
    }

    suspend fun archive(id: Long) = dealDao.archive(id)

    private suspend fun syncInventory(deal: DealEntity) {
        if (deal.isCommissionOnly) return
        val purchased = ProfitCalculator.weight(deal.purchasedQtyKg)
        if (purchased <= BigDecimal.ZERO) return

        val received = ProfitCalculator.weight(deal.receivedQtyKg).let {
            if (it > BigDecimal.ZERO) it else purchased
        }
        val sold = ProfitCalculator.weight(deal.soldQtyKg)
        val remaining = ProfitCalculator.remainingWeight(received, sold)
        val purchaseCost = ProfitCalculator.purchaseCost(
            purchased,
            ProfitCalculator.rate(deal.purchaseRatePerKg)
        )
        val invValue = ProfitCalculator.remainingInventoryValue(received, remaining, purchaseCost)

        val existing = inventoryDao.getByDeal(deal.id)
        val entity = InventoryEntity(
            id = existing?.id ?: 0,
            copperCategory = deal.copperType,
            copperGrade = deal.copperGrade ?: "Unknown",
            sourceDealId = deal.id,
            purchaseDate = deal.dealDate,
            purchasedQtyKg = deal.purchasedQtyKg,
            receivedQtyKg = received.toPlainString(),
            soldQtyKg = sold.toPlainString(),
            remainingQtyKg = remaining.toPlainString(),
            purchaseCost = purchaseCost.toPlainString(),
            inventoryValue = invValue.toPlainString(),
            isClosed = remaining <= BigDecimal.ZERO
        )
        if (existing == null) inventoryDao.insert(entity) else inventoryDao.update(entity)
    }

    fun calculateProfit(deal: DealEntity) = ProfitCalculator.calculateDeal(
        purchasedQty = ProfitCalculator.weight(deal.purchasedQtyKg),
        soldQty = ProfitCalculator.weight(deal.soldQtyKg),
        purchaseRate = ProfitCalculator.rate(deal.purchaseRatePerKg),
        sellingRate = ProfitCalculator.rate(deal.sellingRatePerKg),
        transport = ProfitCalculator.bd(deal.transportCost),
        loadingUnloading = ProfitCalculator.bd(deal.loadingUnloadingCost),
        labour = ProfitCalculator.bd(deal.labourCost),
        weighing = ProfitCalculator.bd(deal.weighingCost),
        packaging = ProfitCalculator.bd(deal.packagingCost),
        commission = ProfitCalculator.bd(deal.commission),
        taxes = ProfitCalculator.bd(deal.taxes),
        miscellaneous = ProfitCalculator.bd(deal.miscellaneousCost),
        expenseCredits = ProfitCalculator.bd(deal.expenseCredits)
    )
}
