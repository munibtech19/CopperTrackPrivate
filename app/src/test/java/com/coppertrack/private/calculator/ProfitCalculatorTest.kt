package com.coppertrack.private.calculator

import com.coppertrack.private.domain.calculator.ProfitCalculator
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class ProfitCalculatorTest {

    @Test
    fun purchaseCost_basic() {
        val cost = ProfitCalculator.purchaseCost(
            ProfitCalculator.weight("100"),
            ProfitCalculator.rate("850")
        )
        assertEquals(BigDecimal("85000.00"), cost)
    }

    @Test
    fun purchaseCost_zeroQty() {
        val cost = ProfitCalculator.purchaseCost(BigDecimal.ZERO, ProfitCalculator.rate("850"))
        assertEquals(ProfitCalculator.ZERO, cost)
    }

    @Test
    fun cogs_partialSale() {
        // Bought 100 kg @ 800 = 80,000. Sold 40 kg → COGS = 32,000
        val cogs = ProfitCalculator.costOfGoodsSold(
            purchasedQty = ProfitCalculator.weight("100"),
            soldQty = ProfitCalculator.weight("40"),
            purchaseCost = BigDecimal("80000.00")
        )
        assertEquals(BigDecimal("32000.00"), cogs)
    }

    @Test
    fun fullDeal_profit() {
        val result = ProfitCalculator.calculateDeal(
            purchasedQty = ProfitCalculator.weight("50"),
            soldQty = ProfitCalculator.weight("50"),
            purchaseRate = ProfitCalculator.rate("900"),
            sellingRate = ProfitCalculator.rate("1100"),
            transport = ProfitCalculator.bd("1500"),
            labour = ProfitCalculator.bd("500")
        )
        // Purchase 45,000; Sales 55,000; COGS 45,000; Expenses 2,000
        // Gross 10,000; Net 8,000
        assertEquals(BigDecimal("45000.00"), result.purchaseCost)
        assertEquals(BigDecimal("55000.00"), result.salesRevenue)
        assertEquals(BigDecimal("10000.00"), result.grossProfit)
        assertEquals(BigDecimal("8000.00"), result.netProfit)
        assertEquals(BigDecimal("160.00"), result.netProfitPerKg)
        assertEquals(BigDecimal("14.55"), result.profitMarginPercent) // 8000/55000*100 ≈ 14.55
    }

    @Test
    fun remainingWeight() {
        val rem = ProfitCalculator.remainingWeight(
            ProfitCalculator.weight("100"),
            ProfitCalculator.weight("35")
        )
        assertEquals(BigDecimal("65.000"), rem)
    }

    @Test
    fun breakEvenRate() {
        // COGS 40k + expenses 2k = 42k / 50 kg = 840
        val rate = ProfitCalculator.breakEvenSellingRate(
            cogs = BigDecimal("40000.00"),
            dealExpenses = BigDecimal("2000.00"),
            soldQty = ProfitCalculator.weight("50")
        )
        assertEquals(0, rate.compareTo(BigDecimal("840.0000")))
    }

    @Test
    fun commissionOnly() {
        val income = ProfitCalculator.commissionIncome(ProfitCalculator.bd("2500"))
        assertEquals(BigDecimal("2500.00"), income)
    }
}
