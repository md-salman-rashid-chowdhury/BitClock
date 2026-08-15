package com.salman.bitclock.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToDiagnostic: () -> Unit,
    onNavigateToAuditLog: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val accountabilityContact by viewModel.accountabilityContact.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("General", style = MaterialTheme.typography.titleMedium)
            
            SettingsItem(
                title = "Diagnostic Dashboard",
                description = "Check permissions and battery optimization status.",
                icon = Icons.Default.HealthAndSafety,
                onClick = onNavigateToDiagnostic
            )

            SettingsItem(
                title = "Security Audit Log",
                description = "View recent security-related actions.",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                onClick = onNavigateToAuditLog
            )

            HorizontalDivider()

            Text("Security & Privacy", style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(
                value = accountabilityContact,
                onValueChange = { viewModel.updateAccountabilityContact(it) },
                label = { Text("Master Accountability Contact") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Default contact for all alarms.") },
                leadingIcon = { Icon(Icons.Default.ContactPhone, contentDescription = null) }
            )

            HorizontalDivider()

            Text("Cloud & Data", style = MaterialTheme.typography.titleMedium)
            
            SettingsItem(
                title = "Backup & Restore",
                description = "Cloud sync (Coming soon)",
                icon = Icons.Default.CloudUpload,
                onClick = { /* Placeholder */ },
                enabled = false
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
