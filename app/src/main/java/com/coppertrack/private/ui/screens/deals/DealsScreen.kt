package com.coppertrack.private.ui.screens.deals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coppertrack.private.CopperTrackApp
import com.coppertrack.private.data.local.entity.DealEntity
import com.coppertrack.private.domain.calculator.ProfitCalculator
import com.coppertrack.private.domain.model.DealStatus
import com.coppertrack.private.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DealsScreen() {
    val app = LocalContext.current.applicationContext as CopperTrackApp
    val deals by app.dealRepository.getAllActive().collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CopperPrimary,
                contentColor = androidx.compose.ui.graphics.Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Deal")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Deals",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (deals.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No deals yet. Tap + to create one.", color = TextSecondary)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(deals, key = { it.id }) { deal ->
                        DealCard(deal, app)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDealDialog(
            onDismiss = { showAddDialog = false },
            onSave = { deal ->
                // Launch coroutine via rememberCoroutineScope in real code
                // For brevity we show the dialog structure
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun DealCard(deal: DealEntity, app: CopperTrackApp) {
    val result = remember(deal) { app.dealRepository.calculateProfit(deal) }
    val dateFmt = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(deal.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                StatusChip(deal.status)
            }
            Text(dateFmt.format(Date(deal.dealDate)), style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Text("${deal.copperGrade ?: "—"} · ${deal.purchasedQtyKg} kg", color = TextSecondary)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net: PKR ${result.netProfit.toPlainString()}", color = if (result.netProfit >= ProfitCalculator.ZERO) ProfitGreen else LossRed)
                Text("Margin: ${result.profitMarginPercent.toPlainString()}%", color = TextSecondary)
            }
        }
    }
}

@Composable
private fun StatusChip(status: DealStatus) {
    val color = when (status) {
        DealStatus.COMPLETED -> ProfitGreen
        DealStatus.CANCELLED -> LossRed
        DealStatus.DRAFT -> TextMuted
        else -> CopperPrimary
    }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.15f)) {
        Text(
            status.name.replace('_', ' '),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun AddDealDialog(onDismiss: () -> Unit, onSave: (DealEntity) -> Unit) {
    var title by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Deal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Purchased Qty (kg)") })
                OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Purchase Rate / kg") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) {
                    onSave(
                        DealEntity(
                            title = title.trim(),
                            dealDate = System.currentTimeMillis(),
                            purchasedQtyKg = qty.ifBlank { "0" },
                            purchaseRatePerKg = rate.ifBlank { "0" },
                            status = DealStatus.DRAFT
                        )
                    )
                }
            }) { Text("Save Draft") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
