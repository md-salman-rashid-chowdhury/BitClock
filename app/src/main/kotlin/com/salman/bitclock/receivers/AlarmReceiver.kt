package com.salman.bitclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.salman.bitclock.di.AppModule
import com.salman.bitclock.services.AlarmRingingService
import com.salman.bitclock.utils.AlarmScheduler
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "com.salman.bitclock.ALARM_TRIGGER") {
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            if (alarmId == -1) return

            val pendingResult = goAsync()
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext, AppModule.AlarmRepositoryEntryPoint::class.java
            )
            val repository = entryPoint.alarmRepository()
            val scheduler = AlarmScheduler(context)

            scope.launch {
                try {
                    val alarm = repository.getAlarmByIdSync(alarmId)
                    if (alarm != null && alarm.isEnabled) {
                        startAlarmService(context, alarm)
                        if (alarm.isRepeating()) {
                            scheduler.scheduleAlarm(alarm)
                        } else {
                            repository.update(alarm.copy(isEnabled = false))
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private fun startAlarmService(context: Context, alarm: com.salman.bitclock.data.models.Alarm) {
        val serviceIntent = Intent(context, AlarmRingingService::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_LABEL", alarm.label)
            putExtra("SOUND_URI", alarm.soundUri)
            putExtra("VIBRATE", alarm.isVibrate)
            putExtra("SNOOZE_MINUTES", alarm.snoozeMinutes)
            putExtra("MISSION_TYPE", alarm.missionType.name)
            putExtra("MISSION_DIFFICULTY", alarm.missionDifficulty)
            putExtra("MISSION_TARGET", alarm.missionTarget)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
