package com.coppertrack.private.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coppertrack.private.ui.theme.*

@Composable
fun ReportsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Reports",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Daily / weekly / monthly reports,\nprofit per kg, expense breakdown,\nCSV export of any report.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
