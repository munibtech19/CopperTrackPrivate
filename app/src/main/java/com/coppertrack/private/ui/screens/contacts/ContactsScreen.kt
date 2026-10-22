package com.coppertrack.private.ui.screens.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coppertrack.private.CopperTrackApp
import com.coppertrack.private.data.local.entity.ContactEntity
import com.coppertrack.private.domain.model.ContactType
import com.coppertrack.private.ui.theme.*

@Composable
fun ContactsScreen() {
    val app = LocalContext.current.applicationContext as CopperTrackApp
    val contacts by app.contactRepository.getAllActive().collectAsStateWithLifecycle(initialValue = emptyList())
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }, containerColor = CopperPrimary) {
                Icon(Icons.Default.Add, "Add Contact", tint = androidx.compose.ui.graphics.Color.Black)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Text("Contacts", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
            if (contacts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No contacts yet.", color = TextSecondary)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(contacts, key = { it.id }) { c -> ContactCard(c) }
                }
            }
        }
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var type by remember { mutableStateOf(ContactType.SUPPLIER) }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("New Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })
                    Row {
                        listOf(ContactType.SUPPLIER, ContactType.BUYER, ContactType.BOTH).forEach { t ->
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(t.name) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        // In real code: coroutineScope.launch { app.contactRepository.save(...) }
                        showAdd = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ContactCard(c: ContactEntity) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkCard)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, null, tint = CopperPrimary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(c.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("${c.type.name} · ${c.phone ?: "—"} · ${c.city ?: ""}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
