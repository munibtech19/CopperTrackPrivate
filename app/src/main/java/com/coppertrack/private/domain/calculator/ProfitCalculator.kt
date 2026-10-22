package com.coppertrack.private.domain.calculator

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * All monetary and weight calculations use BigDecimal.
 * Scale: money = 2 decimal places, weight = 3 decimal places (kg).
 * RoundingMode.HALF_UP is used consistently.
 */
object ProfitCalculator {

    private val MONEY_SCALE = 2
    private val WEIGHT_SCALE = 3
    private val RATE_SCALE = 4
    private val ROUNDING = RoundingMode.HALF_UP

    val ZERO: BigDecimal = BigDecimal.ZERO.setScale(MONEY_SCALE)

    fun bd(value: String?): BigDecimal =
        if (value.isNullOrBlank()) ZERO else BigDecimal(value.trim()).setScale(MONEY_SCALE, ROUNDING)

    fun bd(value: Double?): BigDecimal =
        if (value == null) ZERO else BigDecimal.valueOf(value).setScale(MONEY_SCALE, ROUNDING)

    fun bd(value: Long?): BigDecimal =
        if (value == null) ZERO else BigDecimal.valueOf(value).setScale(MONEY_SCALE, ROUNDING)

    fun weight(value: String?): BigDecimal =
        if (value.isNullOrBlank()) BigDecimal.ZERO.setScale(WEIGHT_SCALE)
        else BigDecimal(value.trim()).setScale(WEIGHT_SCALE, ROUNDING)

    fun rate(value: String?): BigDecimal =
        if (value.isNullOrBlank()) BigDecimal.ZERO.setScale(RATE_SCALE)
        else BigDecimal(value.trim()).setScale(RATE_SCALE, ROUNDING)

    /** Purchase Cost = Purchased Quantity × Purchase Rate */
    fun purchaseCost(purchasedQty: BigDecimal, purchaseRate: BigDecimal): BigDecimal {
        if (purchasedQty <= BigDecimal.ZERO || purchaseRate < BigDecimal.ZERO) return ZERO
        return purchasedQty.multiply(purchaseRate).setScale(MONEY_SCALE, ROUNDING)
    }

    /**
     * Cost of Goods Sold for partial sales.
     * Method: average cost (total purchase cost allocated proportionally to sold quantity).
     * COGS = (Sold Qty / Purchased Qty) × Purchase Cost  when purchasedQty > 0
     */
    fun costOfGoodsSold(
        purchasedQty: BigDecimal,
        soldQty: BigDecimal,
        purchaseCost: BigDecimal
    ): BigDecimal {
        if (purchasedQty <= BigDecimal.ZERO || soldQty <= BigDecimal.ZERO) return ZERO
        val ratio = soldQty.divide(purchasedQty, 10, ROUNDING)
        return purchaseCost.multiply(ratio).setScale(MONEY_SCALE, ROUNDING)
    }

    /** Gross Profit = Sales Revenue − COGS */
    fun grossProfit(salesRevenue: BigDecimal, cogs: BigDecimal): BigDecimal =
        salesRevenue.subtract(cogs).setScale(MONEY_SCALE, ROUNDING)

    /**
     * Deal Expenses = sum of all allocated costs − credits
     */
    fun dealExpenses(
        transport: BigDecimal = ZERO,
        loadingUnloading: BigDecimal = ZERO,
        labour: BigDecimal = ZERO,
        weighing: BigDecimal = ZERO,
        packaging: BigDecimal = ZERO,
        commission: BigDecimal = ZERO,
        taxes: BigDecimal = ZERO,
        miscellaneous: BigDecimal = ZERO,
        expenseCredits: BigDecimal = ZERO
    ): BigDecimal {
        val total = transport
            .add(loadingUnloading)
            .add(labour)
            .add(weighing)
            .add(packaging)
            .add(commission)
            .add(taxes)
            .add(miscellaneous)
            .subtract(expenseCredits)
        return total.setScale(MONEY_SCALE, ROUNDING)
    }

    /** Net Profit = Sales Revenue − COGS − Deal Expenses */
    fun netProfit(salesRevenue: BigDecimal, cogs: BigDecimal, dealExpenses: BigDecimal): BigDecimal =
        salesRevenue.subtract(cogs).subtract(dealExpenses).setScale(MONEY_SCALE, ROUNDING)

    /** Net Profit per kg = Net Profit ÷ Sold Quantity (when sold > 0) */
    fun netProfitPerKg(netProfit: BigDecimal, soldQty: BigDecimal): BigDecimal {
        if (soldQty <= BigDecimal.ZERO) return ZERO
        return netProfit.divide(soldQty, MONEY_SCALE, ROUNDING)
    }

    /** Profit Margin (%) = Net Profit ÷ Sales Revenue × 100 */
    fun profitMarginPercent(netProfit: BigDecimal, salesRevenue: BigDecimal): BigDecimal {
        if (salesRevenue <= BigDecimal.ZERO) return ZERO
        return netProfit
            .multiply(BigDecimal(100))
            .divide(salesRevenue, MONEY_SCALE, ROUNDING)
    }

    /**
     * Break-even Selling Rate per kg =
     * (COGS + Deal Expenses) ÷ Sold Quantity
     */
    fun breakEvenSellingRate(
        cogs: BigDecimal,
        dealExpenses: BigDecimal,
        soldQty: BigDecimal
    ): BigDecimal {
        if (soldQty <= BigDecimal.ZERO) return ZERO
        return cogs.add(dealExpenses).divide(soldQty, RATE_SCALE, ROUNDING)
    }

    /** Remaining inventory weight */
    fun remainingWeight(purchasedQty: BigDecimal, soldQty: BigDecimal): BigDecimal {
        val remaining = purchasedQty.subtract(soldQty)
        return if (remaining < BigDecimal.ZERO) BigDecimal.ZERO.setScale(WEIGHT_SCALE)
        else remaining.setScale(WEIGHT_SCALE, ROUNDING)
    }

    /** Inventory value of remaining stock (using average cost) */
    fun remainingInventoryValue(
        purchasedQty: BigDecimal,
        remainingQty: BigDecimal,
        purchaseCost: BigDecimal
    ): BigDecimal {
        if (purchasedQty <= BigDecimal.ZERO || remainingQty <= BigDecimal.ZERO) return ZERO
        val ratio = remainingQty.divide(purchasedQty, 10, ROUNDING)
        return purchaseCost.multiply(ratio).setScale(MONEY_SCALE, ROUNDING)
    }

    /**
     * Commission-only income (does not affect inventory or COGS).
     */
    fun commissionIncome(commissionAmount: BigDecimal): BigDecimal =
        commissionAmount.setScale(MONEY_SCALE, ROUNDING)

    /**
     * Full result for a deal.
     */
    data class DealProfitResult(
        val purchaseCost: BigDecimal,
        val salesRevenue: BigDecimal,
        val cogs: BigDecimal,
        val dealExpenses: BigDecimal,
        val grossProfit: BigDecimal,
        val netProfit: BigDecimal,
        val netProfitPerKg: BigDecimal,
        val profitMarginPercent: BigDecimal,
        val breakEvenRate: BigDecimal,
        val remainingWeight: BigDecimal,
        val remainingInventoryValue: BigDecimal
    )

    fun calculateDeal(
        purchasedQty: BigDecimal,
        soldQty: BigDecimal,
        purchaseRate: BigDecimal,
        sellingRate: BigDecimal,
        transport: BigDecimal = ZERO,
        loadingUnloading: BigDecimal = ZERO,
        labour: BigDecimal = ZERO,
        weighing: BigDecimal = ZERO,
        packaging: BigDecimal = ZERO,
        commission: BigDecimal = ZERO,
        taxes: BigDecimal = ZERO,
        miscellaneous: BigDecimal = ZERO,
        expenseCredits: BigDecimal = ZERO
    ): DealProfitResult {
        val pCost = purchaseCost(purchasedQty, purchaseRate)
        val salesRev = soldQty.multiply(sellingRate).setScale(MONEY_SCALE, ROUNDING)
        val cogs = costOfGoodsSold(purchasedQty, soldQty, pCost)
        val expenses = dealExpenses(
            transport, loadingUnloading, labour, weighing,
            packaging, commission, taxes, miscellaneous, expenseCredits
        )
        val gross = grossProfit(salesRev, cogs)
        val net = netProfit(salesRev, cogs, expenses)
        val remaining = remainingWeight(purchasedQty, soldQty)

        return DealProfitResult(
            purchaseCost = pCost,
            salesRevenue = salesRev,
            cogs = cogs,
            dealExpenses = expenses,
            grossProfit = gross,
            netProfit = net,
            netProfitPerKg = netProfitPerKg(net, soldQty),
            profitMarginPercent = profitMarginPercent(net, salesRev),
            breakEvenRate = breakEvenSellingRate(cogs, expenses, soldQty),
            remainingWeight = remaining,
            remainingInventoryValue = remainingInventoryValue(purchasedQty, remaining, pCost)
        )
    }
}
