package com.salman.bitclock.ui.alarm

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salman.bitclock.data.models.Alarm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailScreen(
    alarmId: Int?,
    onBack: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val alarmState by viewModel.alarms.collectAsState()
    val existingAlarm = alarmState.find { it.id == alarmId }

    var hour by remember { mutableIntStateOf(existingAlarm?.hour ?: 12) }
    var minute by remember { mutableIntStateOf(existingAlarm?.minute ?: 0) }
    var label by remember { mutableStateOf(existingAlarm?.label ?: "") }
    var isVibrate by remember { mutableStateOf(existingAlarm?.isVibrate ?: true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId == null) "New Alarm" else "Edit Alarm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (alarmId == null) {
                            viewModel.addAlarm(Alarm(hour = hour, minute = minute, label = label, isVibrate = isVibrate))
                        } else {
                            existingAlarm?.let {
                                viewModel.updateAlarm(it.copy(hour = hour, minute = minute, label = label, isVibrate = isVibrate))
                            }
                        }
                        onBack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern Material 3 Time Picker
            val timePickerState = rememberTimePickerState(
                initialHour = hour,
                initialMinute = minute,
                is24Hour = false
            )

            TimePicker(state = timePickerState)

            Spacer(Modifier.height(24.dp))
            
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vibrate", modifier = Modifier.weight(1f))
                Switch(checked = isVibrate, onCheckedChange = { isVibrate = it })
            }

            // Update local state when picker changes (for saving)
            LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                hour = timePickerState.hour
                minute = timePickerState.minute
            }
        }
    }
}
