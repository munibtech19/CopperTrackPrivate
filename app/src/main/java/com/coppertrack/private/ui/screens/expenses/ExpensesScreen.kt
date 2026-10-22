package com.coppertrack.private.ui.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coppertrack.private.ui.theme.*

@Composable
fun ExpensesScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Expenses & Payments",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Expense tracker and cash ledger.\nAdd expenses, record supplier payments and buyer receipts.\nAll data stored locally in Room.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
