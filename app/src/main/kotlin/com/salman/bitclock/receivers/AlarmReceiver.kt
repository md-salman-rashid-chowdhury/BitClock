package com.salman.bitclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.salman.bitclock.di.AppModule
import com.salman.bitclock.services.AccountabilityWorker
import com.salman.bitclock.services.AlarmRingingService
import com.salman.bitclock.utils.AlarmScheduler
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == "com.salman.bitclock.ALARM_TRIGGER") {
            handleAlarmTrigger(context, intent)
        } else if (action == "com.salman.bitclock.PRE_ALARM_TRIGGER") {
            handlePreAlarmTrigger(context, intent)
        }
    }

    private fun handleAlarmTrigger(context: Context, intent: Intent) {
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
                    
                    // Schedule accountability worker if contact is set
                    if (!alarm.accountabilityContact.isNullOrBlank()) {
                        scheduleAccountabilityWorker(context, alarm)
                    }

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

    private fun handlePreAlarmTrigger(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "pre_alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(channelId, "Pre-Alarm Alerts", android.app.NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setContentTitle("Gentle Wake Up")
            .setContentText("Your alarm will ring soon.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setVibrate(longArrayOf(0, 500, 1000, 500))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(alarmId + 200000, notification)
    }

    private fun scheduleAccountabilityWorker(context: Context, alarm: com.salman.bitclock.data.models.Alarm) {
        val workData = Data.Builder()
            .putString("CONTACT", alarm.accountabilityContact)
            .putString("LABEL", alarm.label)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AccountabilityWorker>()
            .setInitialDelay(alarm.accountabilityDelayMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "accountability_${alarm.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun startAlarmService(context: Context, alarm: com.salman.bitclock.data.models.Alarm) {
        val serviceIntent = Intent(context, AlarmRingingService::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_LABEL", alarm.label)
            putExtra("SOUND_URI", alarm.soundUri)
            putExtra("VIBRATE", alarm.isVibrate)
            putExtra("SNOOZE_MINUTES", alarm.snoozeMinutes)
            putExtra("SNOOZE_COUNT", alarm.snoozeCount)
            putExtra("SNOOZE_LIMIT", alarm.snoozeLimit)
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
