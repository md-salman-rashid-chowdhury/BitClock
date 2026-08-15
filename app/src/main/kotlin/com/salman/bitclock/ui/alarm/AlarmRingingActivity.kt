package com.salman.bitclock.ui.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salman.bitclock.data.models.MissionType
import com.salman.bitclock.services.AlarmRingingService
import com.salman.bitclock.ui.alarm.missions.MathMission
import com.salman.bitclock.ui.alarm.missions.ShakeMission
import com.salman.bitclock.ui.theme.BitClockTheme

class AlarmRingingActivity : ComponentActivity() {
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

        val label = intent.getStringExtra("ALARM_LABEL") ?: "Alarm"
        val missionTypeStr = intent.getStringExtra("MISSION_TYPE") ?: MissionType.NONE.name
        val missionType = try {
            MissionType.valueOf(missionTypeStr)
        } catch (_: Exception) {
            MissionType.NONE
        }
        val difficulty = intent.getIntExtra("MISSION_DIFFICULTY", 1)

        setContent {
            BitClockTheme {
                AlarmRingingScreen(
                    label = label,
                    missionType = missionType,
                    difficulty = difficulty,
                    onDismiss = {
                        stopService(Intent(this@AlarmRingingActivity, AlarmRingingService::class.java))
                        finishAndRemoveTask()
                    }
                )
            }
        }
    }
}

@Composable
fun AlarmRingingScreen(
    label: String,
    missionType: MissionType,
    difficulty: Int,
    onDismiss: () -> Unit
) {
    var isMissionComplete by remember { mutableStateOf(missionType == MissionType.NONE) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isMissionComplete) {
                when (missionType) {
                    MissionType.MATH -> MathMission(
                        difficulty = difficulty,
                        onComplete = { isMissionComplete = true }
                    )
                    MissionType.SHAKE -> ShakeMission(
                        difficulty = difficulty,
                        onComplete = { isMissionComplete = true }
                    )
                    else -> LaunchedEffect(Unit) { isMissionComplete = true }
                }
            } else {
                Text(text = label, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Dismiss", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
