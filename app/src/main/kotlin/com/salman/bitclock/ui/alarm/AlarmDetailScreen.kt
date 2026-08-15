package com.salman.bitclock.ui.alarm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.models.MissionType

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
    var missionType by remember { mutableStateOf(existingAlarm?.missionType ?: MissionType.NONE) }
    var missionDifficulty by remember { mutableIntStateOf(existingAlarm?.missionDifficulty ?: 1) }
    var missionTarget by remember { mutableStateOf(existingAlarm?.missionTarget ?: "") }
    var snoozeLimit by remember { mutableIntStateOf(existingAlarm?.snoozeLimit ?: 3) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId == null) "New Alarm" else "Edit Alarm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (alarmId == null) {
                            viewModel.addAlarm(
                                Alarm(
                                    hour = hour,
                                    minute = minute,
                                    label = label,
                                    isVibrate = isVibrate,
                                    missionType = missionType,
                                    missionDifficulty = missionDifficulty,
                                    missionTarget = missionTarget,
                                    snoozeLimit = snoozeLimit
                                )
                            )
                        } else {
                            existingAlarm?.let {
                                viewModel.updateAlarm(
                                    it.copy(
                                        hour = hour,
                                        minute = minute,
                                        label = label,
                                        isVibrate = isVibrate,
                                        missionType = missionType,
                                        missionDifficulty = missionDifficulty,
                                        missionTarget = missionTarget,
                                        snoozeLimit = snoozeLimit
                                    )
                                )
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Time", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(value = hour, onValueChange = { hour = it }, range = 0..23, label = "Hour")
                Text(":", style = MaterialTheme.typography.headlineLarge)
                NumberPicker(value = minute, onValueChange = { minute = it }, range = 0..59, label = "Min")
            }

            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vibrate", modifier = Modifier.weight(1f))
                Switch(checked = isVibrate, onCheckedChange = { isVibrate = it })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Snooze Limit (0 for infinite)", modifier = Modifier.weight(1f))
                NumberPicker(value = snoozeLimit, onValueChange = { snoozeLimit = it }, range = 0..10, label = "")
            }

            HorizontalDivider()

            Text("Mission", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MissionType.entries.forEach { type ->
                    FilterChip(
                        selected = missionType == type,
                        onClick = { missionType = type },
                        label = { Text(type.name) }
                    )
                }
            }

            if (missionType != MissionType.NONE) {
                if (missionType == MissionType.BARCODE) {
                    OutlinedTextField(
                        value = missionTarget,
                        onValueChange = { missionTarget = it },
                        label = { Text("Target Barcode/QR Content") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("Scan or type the content of the object to scan later.") }
                    )
                } else {
                    Text("Difficulty: ${when(missionDifficulty) { 1 -> "Easy"; 2 -> "Medium"; else -> "Hard" }}")
                    Slider(
                        value = missionDifficulty.toFloat(),
                        onValueChange = { missionDifficulty = it.toInt() },
                        valueRange = 1f..3f,
                        steps = 1
                    )
                }
            }
        }
    }
}

@Composable
fun NumberPicker(value: Int, onValueChange: (Int) -> Unit, range: IntRange, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.displayMedium
            )
            Column {
                IconButton(onClick = { if (value < range.last) onValueChange(value + 1) else onValueChange(range.first) }) {
                    Text("+")
                }
                IconButton(onClick = { if (value > range.first) onValueChange(value - 1) else onValueChange(range.last) }) {
                    Text("-")
                }
            }
        }
    }
}
