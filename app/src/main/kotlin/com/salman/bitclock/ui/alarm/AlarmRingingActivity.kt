package com.salman.bitclock.ui.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salman.bitclock.data.models.Habit
import com.salman.bitclock.data.models.MissionType
import com.salman.bitclock.data.repository.AlarmRepository
import com.salman.bitclock.data.repository.HabitRepository
import com.salman.bitclock.services.AlarmRingingService
import com.salman.bitclock.ui.alarm.missions.BarcodeMission
import com.salman.bitclock.ui.alarm.missions.MathMission
import com.salman.bitclock.ui.alarm.missions.ShakeMission
import com.salman.bitclock.ui.theme.BitClockTheme
import com.salman.bitclock.utils.AlarmScheduler
import com.salman.bitclock.utils.VoiceBriefingManager
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    @Inject
    lateinit var repository: AlarmRepository
    
    @Inject
    lateinit var scheduler: AlarmScheduler

    @Inject
    lateinit var voiceBriefingManager: VoiceBriefingManager

    @Inject
    lateinit var habitRepository: HabitRepository

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            Toast.makeText(this, "Dismiss the alarm first to use other system features!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val keyguardManager = getSystemService(android.app.KeyguardManager::class.java)
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val label = intent.getStringExtra("ALARM_LABEL") ?: "Alarm"
        val missionTypeStr = intent.getStringExtra("MISSION_TYPE") ?: MissionType.NONE.name
        val missionType = try {
            MissionType.valueOf(missionTypeStr)
        } catch (_: Exception) {
            MissionType.NONE
        }
        val difficulty = intent.getIntExtra("MISSION_DIFFICULTY", 1)
        val target = intent.getStringExtra("MISSION_TARGET") ?: ""
        val snoozeCount = intent.getIntExtra("SNOOZE_COUNT", 0)
        val snoozeLimit = intent.getIntExtra("SNOOZE_LIMIT", 3)
        val snoozeMinutes = intent.getIntExtra("SNOOZE_MINUTES", 10)

        setContent {
            val habits by if (alarmId != -1) {
                habitRepository.getHabitsForAlarm(alarmId).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Habit>()) }
            }

            BitClockTheme {
                AlarmRingingScreen(
                    label = label,
                    missionType = missionType,
                    difficulty = difficulty,
                    target = target,
                    snoozeCount = snoozeCount,
                    snoozeLimit = snoozeLimit,
                    habits = habits,
                    onDismiss = {
                        dismissAlarm(alarmId, label)
                    },
                    onSnooze = {
                        snoozeAlarm(alarmId, snoozeCount, snoozeMinutes)
                    }
                )
            }
        }
    }

    private fun dismissAlarm(alarmId: Int, label: String) {
        CoroutineScope(Dispatchers.IO).launch {
            voiceBriefingManager.speakBriefing(label)
            if (alarmId != -1) {
                val alarm = repository.getAlarmByIdSync(alarmId)
                if (alarm != null) {
                    var newDifficulty = alarm.missionDifficulty
                    var newHistoryCount = alarm.dismissalHistoryCount
                    
                    if (alarm.adaptiveDifficultyEnabled) {
                        newHistoryCount++
                        if (newHistoryCount >= 3 && newDifficulty < 3) {
                            newDifficulty++
                            newHistoryCount = 0
                        }
                    }
                    
                    repository.update(alarm.copy(
                        snoozeCount = 0,
                        missionDifficulty = newDifficulty,
                        dismissalHistoryCount = newHistoryCount
                    ))
                }
                WorkManager.getInstance(applicationContext).cancelUniqueWork("accountability_$alarmId")
            }
            stopService(Intent(this@AlarmRingingActivity, AlarmRingingService::class.java))
            finishAndRemoveTask()
        }
    }

    private fun snoozeAlarm(alarmId: Int, currentSnoozeCount: Int, snoozeMinutes: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (alarmId != -1) {
                val alarm = repository.getAlarmByIdSync(alarmId)
                if (alarm != null) {
                    val updatedAlarm = alarm.copy(snoozeCount = currentSnoozeCount + 1)
                    repository.update(updatedAlarm)
                    
                    val triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
                    scheduler.scheduleAlarmAtTime(alarmId, triggerTime, alarm.label)
                }
            }
            stopService(Intent(this@AlarmRingingActivity, AlarmRingingService::class.java))
            finishAndRemoveTask()
        }
    }
}

@Composable
fun AlarmRingingScreen(
    label: String,
    missionType: MissionType,
    difficulty: Int,
    target: String,
    snoozeCount: Int,
    snoozeLimit: Int,
    habits: List<Habit>,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    var checkedHabits by remember(habits) { 
        mutableStateOf(habits.associate { it.id to false }) 
    }
    
    val allHabitsCompleted = checkedHabits.values.all { it }
    var isMissionComplete by remember { mutableStateOf(missionType == MissionType.NONE) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (habits.isNotEmpty() && !allHabitsCompleted) {
                Text(text = "Morning Habits", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                habits.forEach { habit ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = checkedHabits[habit.id] ?: false,
                            onCheckedChange = { checked ->
                                checkedHabits = checkedHabits.toMutableMap().apply { put(habit.id, checked) }
                            }
                        )
                        Text(text = habit.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else if (!isMissionComplete) {
                when (missionType) {
                    MissionType.MATH -> MathMission(
                        difficulty = difficulty,
                        onComplete = { isMissionComplete = true }
                    )
                    MissionType.SHAKE -> ShakeMission(
                        difficulty = difficulty,
                        onComplete = { isMissionComplete = true }
                    )
                    MissionType.BARCODE -> BarcodeMission(
                        targetBarcode = target,
                        onComplete = { isMissionComplete = true }
                    )
                    else -> LaunchedEffect(Unit) { isMissionComplete = true }
                }
            } else {
                Text(text = label, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Dismiss", style = MaterialTheme.typography.titleMedium)
                }
                
                if (snoozeLimit == 0 || snoozeCount < snoozeLimit) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onSnooze,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        val limitText = if (snoozeLimit > 0) " ($snoozeCount/$snoozeLimit)" else ""
                        Text("Snooze$limitText", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
