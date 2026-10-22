package com.coppertrack.private.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coppertrack.private.ui.theme.*

@Composable
fun SettingsScreen() {
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "More / Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
        }
        item { SettingsItem(Icons.Default.Business, "Business Name", "My Copper Business") }
        item { SettingsItem(Icons.Default.AttachMoney, "Currency", "PKR") }
        item { SettingsItem(Icons.Default.Scale, "Weight Unit", "kg") }
        item { SettingsItem(Icons.Default.Lock, "PIN Lock", "Not set") }
        item { SettingsItem(Icons.Default.Fingerprint, "Biometric", "Off") }
        item { SettingsItem(Icons.Default.Backup, "Backup & Restore", "Local only") }
        item { SettingsItem(Icons.Default.ImportExport, "Import / Export CSV", "Deals, Contacts, Expenses") }
        item { SettingsItem(Icons.Default.Info, "Version", "1.0.0") }
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "All data stays on this device. No cloud, no accounts, no trackers.\nUninstalling the app without a backup will permanently delete your data.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = MaterialTheme.shapes.medium
    ) {
        ListItem(
            headlineContent = { Text(title, color = TextPrimary) },
            supportingContent = { Text(subtitle, color = TextSecondary) },
            leadingContent = { Icon(icon, null, tint = CopperPrimary) },
            colors = ListItemDefaults.colors(containerColor = DarkCard)
        )
    }
}
