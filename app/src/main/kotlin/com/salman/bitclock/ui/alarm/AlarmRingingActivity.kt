package com.salman.bitclock.ui.alarm

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salman.bitclock.ui.theme.BitClockTheme

class AlarmRingingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val label = intent.getStringExtra("ALARM_LABEL") ?: "Alarm"

        setContent {
            BitClockTheme {
                AlarmRingingScreen(label = label, onDismiss = { finishAndRemoveTask() })
            }
        }
    }
}

@Composable
fun AlarmRingingScreen(label: String, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Dismiss")
            }
        }
    }
}
