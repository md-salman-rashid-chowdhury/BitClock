package com.salman.bitclock.ui.alarm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.models.Habit
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
    var smartWakeEnabled by remember { mutableStateOf(existingAlarm?.smartWakeEnabled ?: false) }
    var smartWakeWindow by remember { mutableIntStateOf(existingAlarm?.smartWakeWindowMinutes ?: 20) }
    var accountabilityContact by remember { 
        mutableStateOf(existingAlarm?.accountabilityContact ?: viewModel.getDefaultAccountabilityContact()) 
    }
    var accountabilityDelay by remember { mutableIntStateOf(existingAlarm?.accountabilityDelayMinutes ?: 10) }
    var preAlarmEnabled by remember { mutableStateOf(existingAlarm?.preAlarmEnabled ?: false) }
    var preAlarmMinutes by remember { mutableIntStateOf(existingAlarm?.preAlarmMinutes ?: 5) }
    var adaptiveDifficultyEnabled by remember { mutableStateOf(existingAlarm?.adaptiveDifficultyEnabled ?: false) }

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
                                    snoozeLimit = snoozeLimit,
                                    smartWakeEnabled = smartWakeEnabled,
                                    smartWakeWindowMinutes = smartWakeWindow,
                                    accountabilityContact = accountabilityContact.ifBlank { null },
                                    accountabilityDelayMinutes = accountabilityDelay,
                                    preAlarmEnabled = preAlarmEnabled,
                                    preAlarmMinutes = preAlarmMinutes,
                                    adaptiveDifficultyEnabled = adaptiveDifficultyEnabled
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
                                        snoozeLimit = snoozeLimit,
                                        smartWakeEnabled = smartWakeEnabled,
                                        smartWakeWindowMinutes = smartWakeWindow,
                                        accountabilityContact = accountabilityContact.ifBlank { null },
                                        accountabilityDelayMinutes = accountabilityDelay,
                                        preAlarmEnabled = preAlarmEnabled,
                                        preAlarmMinutes = preAlarmMinutes,
                                        adaptiveDifficultyEnabled = adaptiveDifficultyEnabled
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Smart Wake", modifier = Modifier.weight(1f))
                Switch(checked = smartWakeEnabled, onCheckedChange = { smartWakeEnabled = it })
            }

            if (smartWakeEnabled) {
                Text("Smart Wake Window: $smartWakeWindow min")
                Slider(
                    value = smartWakeWindow.toFloat(),
                    onValueChange = { smartWakeWindow = it.toInt() },
                    valueRange = 10f..60f,
                    steps = 10
                )
            }

            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pre-Alarm", modifier = Modifier.weight(1f))
                Switch(checked = preAlarmEnabled, onCheckedChange = { preAlarmEnabled = it })
            }

            if (preAlarmEnabled) {
                Text("Pre-Alarm: $preAlarmMinutes min before")
                Slider(
                    value = preAlarmMinutes.toFloat(),
                    onValueChange = { preAlarmMinutes = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 14
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Adaptive Difficulty", modifier = Modifier.weight(1f))
                Switch(checked = adaptiveDifficultyEnabled, onCheckedChange = { adaptiveDifficultyEnabled = it })
            }

            HorizontalDivider()

            if (alarmId != null) {
                Text("Morning Habits", style = MaterialTheme.typography.titleMedium)
                val habits by viewModel.getHabitsForAlarm(alarmId).collectAsState(initial = emptyList())
                var newHabitName by remember { mutableStateOf("") }

                habits.forEach { habit ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = habit.name, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteHabit(habit) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Habit")
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("New Habit") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newHabitName.isNotBlank()) {
                            viewModel.addHabit(Habit(alarmId = alarmId, name = newHabitName))
                            newHabitName = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Habit")
                    }
                }
                
                HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pre-Alarm", modifier = Modifier.weight(1f))
                Switch(checked = preAlarmEnabled, onCheckedChange = { preAlarmEnabled = it })
            }

            if (preAlarmEnabled) {
                Text("Pre-Alarm: $preAlarmMinutes min before")
                Slider(
                    value = preAlarmMinutes.toFloat(),
                    onValueChange = { preAlarmMinutes = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 14
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Adaptive Difficulty", modifier = Modifier.weight(1f))
                Switch(checked = adaptiveDifficultyEnabled, onCheckedChange = { adaptiveDifficultyEnabled = it })
            }

            HorizontalDivider()
            }

            Text("Shared Accountability", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = accountabilityContact,
                onValueChange = { accountabilityContact = it },
                label = { Text("Contact (Email or Phone)") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Notified if alarm isn't dismissed.") }
            )
            
            if (accountabilityContact.isNotBlank()) {
                Text("Delay: $accountabilityDelay min")
                Slider(
                    value = accountabilityDelay.toFloat(),
                    onValueChange = { accountabilityDelay = it.toInt() },
                    valueRange = 1f..30f,
                    steps = 29
                )
            }

            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pre-Alarm", modifier = Modifier.weight(1f))
                Switch(checked = preAlarmEnabled, onCheckedChange = { preAlarmEnabled = it })
            }

            if (preAlarmEnabled) {
                Text("Pre-Alarm: $preAlarmMinutes min before")
                Slider(
                    value = preAlarmMinutes.toFloat(),
                    onValueChange = { preAlarmMinutes = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 14
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Adaptive Difficulty", modifier = Modifier.weight(1f))
                Switch(checked = adaptiveDifficultyEnabled, onCheckedChange = { adaptiveDifficultyEnabled = it })
            }

            HorizontalDivider()

            if (alarmId != null) {
                Text("Morning Habits", style = MaterialTheme.typography.titleMedium)
                val habits by viewModel.getHabitsForAlarm(alarmId).collectAsState(initial = emptyList())
                var newHabitName by remember { mutableStateOf("") }

                habits.forEach { habit ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = habit.name, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteHabit(habit) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Habit")
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("New Habit") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newHabitName.isNotBlank()) {
                            viewModel.addHabit(Habit(alarmId = alarmId, name = newHabitName))
                            newHabitName = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Habit")
                    }
                }
                
                HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pre-Alarm", modifier = Modifier.weight(1f))
                Switch(checked = preAlarmEnabled, onCheckedChange = { preAlarmEnabled = it })
            }

            if (preAlarmEnabled) {
                Text("Pre-Alarm: $preAlarmMinutes min before")
                Slider(
                    value = preAlarmMinutes.toFloat(),
                    onValueChange = { preAlarmMinutes = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 14
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Adaptive Difficulty", modifier = Modifier.weight(1f))
                Switch(checked = adaptiveDifficultyEnabled, onCheckedChange = { adaptiveDifficultyEnabled = it })
            }

            HorizontalDivider()
            }

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
