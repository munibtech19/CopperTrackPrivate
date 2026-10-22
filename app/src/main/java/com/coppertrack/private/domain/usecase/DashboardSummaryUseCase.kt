package com.coppertrack.private.domain.usecase

import com.coppertrack.private.data.local.dao.*
import com.coppertrack.private.data.local.entity.DealEntity
import com.coppertrack.private.domain.calculator.ProfitCalculator
import com.coppertrack.private.domain.model.DealStatus
import com.coppertrack.private.domain.model.PaymentDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal

data class DashboardSummary(
    val totalPurchaseValue: BigDecimal = ProfitCalculator.ZERO,
    val totalSalesValue: BigDecimal = ProfitCalculator.ZERO,
    val totalExpenses: BigDecimal = ProfitCalculator.ZERO,
    val grossProfit: BigDecimal = ProfitCalculator.ZERO,
    val netProfit: BigDecimal = ProfitCalculator.ZERO,
    val cashAvailable: BigDecimal = ProfitCalculator.ZERO,
    val receivables: BigDecimal = ProfitCalculator.ZERO,
    val payables: BigDecimal = ProfitCalculator.ZERO,
    val pendingDeals: Int = 0,
    val completedDeals: Int = 0,
    val totalRemainingStockKg: BigDecimal = ProfitCalculator.ZERO,
    val inventoryValue: BigDecimal = ProfitCalculator.ZERO
)

class DashboardSummaryUseCase(
    private val dealDao: DealDao,
    private val expenseDao: ExpenseDao,
    private val paymentDao: PaymentDao,
    private val inventoryDao: InventoryDao,
    private val settingsDao: SettingsDao
) {
    fun observe(): Flow<DashboardSummary> = combine(
        dealDao.getAllActive(),
        expenseDao.getAll(),
        paymentDao.getAllActive(),
        inventoryDao.getOpenStock(),
        settingsDao.getSettings()
    ) { deals, expenses, payments, inventory, settings ->
        compute(deals, expenses, payments, inventory, settings?.openingBalance ?: "0")
    }

    private fun compute(
        deals: List<DealEntity>,
        expenses: List<com.coppertrack.private.data.local.entity.ExpenseEntity>,
        payments: List<com.coppertrack.private.data.local.entity.PaymentEntity>,
        inventory: List<com.coppertrack.private.data.local.entity.InventoryEntity>,
        openingBalanceStr: String
    ): DashboardSummary {
        var totalPurchase = ProfitCalculator.ZERO
        var totalSales = ProfitCalculator.ZERO
        var totalGross = ProfitCalculator.ZERO
        var totalNet = ProfitCalculator.ZERO
        var pending = 0
        var completed = 0

        deals.forEach { deal ->
            if (deal.isCommissionOnly) {
                val commission = ProfitCalculator.bd(deal.commission)
                totalNet = totalNet.add(commission)
                return@forEach
            }
            val result = ProfitCalculator.calculateDeal(
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
            totalPurchase = totalPurchase.add(result.purchaseCost)
            totalSales = totalSales.add(result.salesRevenue)
            totalGross = totalGross.add(result.grossProfit)
            totalNet = totalNet.add(result.netProfit)

            when (deal.status) {
                DealStatus.COMPLETED -> completed++
                DealStatus.CANCELLED, DealStatus.DRAFT -> {}
                else -> pending++
            }
        }

        // Unlinked overhead expenses only (avoid double-counting deal expenses)
        val overhead = expenses
            .filter { !it.isLinkedToDealExpense }
            .fold(ProfitCalculator.ZERO) { acc, e -> acc.add(ProfitCalculator.bd(e.amount)) }

        val netAfterOverhead = totalNet.subtract(overhead)

        val inflows = payments
            .filter { it.direction == PaymentDirection.INFLOW }
            .fold(ProfitCalculator.ZERO) { acc, p -> acc.add(ProfitCalculator.bd(p.amount)) }
        val outflows = payments
            .filter { it.direction == PaymentDirection.OUTFLOW }
            .fold(ProfitCalculator.ZERO) { acc, p -> acc.add(ProfitCalculator.bd(p.amount)) }

        val opening = ProfitCalculator.bd(openingBalanceStr)
        val cash = opening.add(inflows).subtract(outflows)

        // Simplified receivables / payables (sales - received vs purchase - paid)
        // In a full system this would be per-contact running balances.
        val totalReceivedFromBuyers = inflows // approximate
        val totalPaidToSuppliers = outflows
        val receivables = totalSales.subtract(totalReceivedFromBuyers).coerceAtLeast(ProfitCalculator.ZERO)
        val payables = totalPurchase.subtract(totalPaidToSuppliers).coerceAtLeast(ProfitCalculator.ZERO)

        val stockKg = inventory.fold(ProfitCalculator.ZERO) { acc, i ->
            acc.add(ProfitCalculator.weight(i.remainingQtyKg))
        }
        val invValue = inventory.fold(ProfitCalculator.ZERO) { acc, i ->
            acc.add(ProfitCalculator.bd(i.inventoryValue))
        }

        return DashboardSummary(
            totalPurchaseValue = totalPurchase,
            totalSalesValue = totalSales,
            totalExpenses = overhead,
            grossProfit = totalGross,
            netProfit = netAfterOverhead,
            cashAvailable = cash,
            receivables = receivables,
            payables = payables,
            pendingDeals = pending,
            completedDeals = completed,
            totalRemainingStockKg = stockKg,
            inventoryValue = invValue
        )
    }
}
