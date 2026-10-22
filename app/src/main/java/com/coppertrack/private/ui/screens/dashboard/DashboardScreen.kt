package com.coppertrack.private.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coppertrack.private.CopperTrackApp
import com.coppertrack.private.domain.calculator.ProfitCalculator
import com.coppertrack.private.domain.usecase.DashboardSummary
import com.coppertrack.private.ui.theme.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen() {
    val app = LocalContext.current.applicationContext as CopperTrackApp
    val summary by app.dashboardUseCase.observe().collectAsStateWithLifecycle(
        initialValue = DashboardSummary()
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "CopperTrack Private",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Business Overview",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Net Profit",
                    value = formatMoney(summary.netProfit),
                    icon = Icons.Default.TrendingUp,
                    accent = if (summary.netProfit >= BigDecimal.ZERO) ProfitGreen else LossRed,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Cash Available",
                    value = formatMoney(summary.cashAvailable),
                    icon = Icons.Default.AccountBalanceWallet,
                    accent = CopperPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Gross Profit",
                    value = formatMoney(summary.grossProfit),
                    icon = Icons.Default.ShowChart,
                    accent = ProfitGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Expenses",
                    value = formatMoney(summary.totalExpenses),
                    icon = Icons.Default.Receipt,
                    accent = LossRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionCard(title = "Sales & Purchases") {
                InfoRow("Total Purchase Value", formatMoney(summary.totalPurchaseValue))
                InfoRow("Total Sales Value", formatMoney(summary.totalSalesValue))
                InfoRow("Receivables (Buyers)", formatMoney(summary.receivables), ProfitGreen)
                InfoRow("Payables (Suppliers)", formatMoney(summary.payables), LossRed)
            }
        }

        item {
            SectionCard(title = "Deals & Inventory") {
                InfoRow("Pending Deals", summary.pendingDeals.toString())
                InfoRow("Completed Deals", summary.completedDeals.toString())
                InfoRow("Remaining Stock", "${summary.totalRemainingStockKg.toPlainString()} kg")
                InfoRow("Inventory Value", formatMoney(summary.inventoryValue))
            }
        }

        item {
            if (summary.pendingDeals == 0 && summary.completedDeals == 0) {
                EmptyStateCard(
                    title = "No deals yet",
                    message = "Create your first deal from the Deals tab to see live numbers here."
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptyStateCard(title: String, message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Inbox, null, tint = TextMuted, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

private fun formatMoney(value: BigDecimal): String {
    val nf = NumberFormat.getNumberInstance(Locale("en", "PK"))
    nf.minimumFractionDigits = 0
    nf.maximumFractionDigits = 2
    return "PKR ${nf.format(value)}"
}
