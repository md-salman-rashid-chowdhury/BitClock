package com.salman.bitclock.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToDiagnostic: () -> Unit,
    onNavigateToAuditLog: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val accountabilityContact by viewModel.accountabilityContact.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showImportConfirm by remember { mutableStateOf(false) }
    var pendingImportJson by remember { mutableStateOf("") }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                val json = viewModel.exportBackup()
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(json.toByteArray())
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val json = stream.readBytes().decodeToString()
                    pendingImportJson = json
                    showImportConfirm = true
                }
            }
        }
    }

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
                title = "Export Backup",
                description = "Save your alarms and settings to a JSON file.",
                icon = Icons.Default.CloudDownload,
                onClick = { exportLauncher.launch("BitClock_Backup.json") }
            )

            SettingsItem(
                title = "Import Backup",
                description = "Restore alarms and settings from a JSON file.",
                icon = Icons.Default.CloudUpload,
                onClick = { importLauncher.launch(arrayOf("application/json")) }
            )

            SettingsItem(
                title = "Cloud Sync",
                description = "Securely sync data with BitClock Cloud.",
                icon = Icons.Default.Sync,
                onClick = {
                    scope.launch {
                        val success = viewModel.syncWithCloud()
                        if (success) {
                            // Show success message
                        }
                    }
                }
            )
        }

        if (showImportConfirm) {
            AlertDialog(
                onDismissRequest = { showImportConfirm = false },
                title = { Text("Confirm Import") },
                text = { Text("Importing data will overwrite all current alarms and settings. This cannot be undone.") },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            viewModel.importBackup(pendingImportJson)
                            showImportConfirm = false
                        }
                    }) {
                        Text("Import")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportConfirm = false }) {
                        Text("Cancel")
                    }
                }
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
