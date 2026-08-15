package com.salman.bitclock.ui.diagnostic

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.salman.bitclock.utils.BatteryOptimizationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticScreen(
    onBack: () -> Unit,
    viewModel: DiagnosticViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alarm Reliability Diagnostic") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DiagnosticItem(
                title = "Exact Alarm Permission",
                description = "Required to fire alarms at the exact scheduled time.",
                isGranted = state.isExactAlarmPermissionGranted,
                onFix = {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = "package:${context.packageName}".toUri()
                        }
                        context.startActivity(intent)
                    }
                }
            )

            DiagnosticItem(
                title = "Notification Permission",
                description = "Required to show alarm alerts and controls.",
                isGranted = state.isNotificationPermissionGranted,
                onFix = {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                }
            )

            DiagnosticItem(
                title = "Battery Optimization",
                description = "Excluding BitClock from battery optimization ensures alarms aren't killed by the system.",
                isGranted = state.isIgnoringBatteryOptimizations,
                onFix = {
                    try {
                        context.startActivity(BatteryOptimizationHelper.getRequestIgnoreBatteryOptimizationsIntent(context))
                    } catch (_: Exception) {
                        context.startActivity(BatteryOptimizationHelper.getBatteryOptimizationSettingsIntent())
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.refreshState() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Status")
            }
        }
    }
}

@Composable
fun DiagnosticItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onFix: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = Color(0xFF4CAF50)
                )
            } else {
                IconButton(onClick = onFix) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Fix Issue",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
