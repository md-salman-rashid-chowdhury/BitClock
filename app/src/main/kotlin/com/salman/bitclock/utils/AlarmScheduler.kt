package com.salman.bitclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.salman.bitclock.MainActivity
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.receivers.AlarmReceiver

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val TAG = "AlarmScheduler"

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() == true
        } else {
            true
        }
    }

    fun scheduleAlarm(alarm: Alarm) {
        val triggerTime = alarm.getNextAlarmTime()
        scheduleAlarmAtTime(alarm.id, triggerTime, alarm.label)
        
        if (alarm.preAlarmEnabled) {
            val preAlarmTime = triggerTime - (alarm.preAlarmMinutes * 60 * 1000)
            if (preAlarmTime > System.currentTimeMillis()) {
                schedulePreAlarm(alarm.id, preAlarmTime)
            }
        }
    }

    private fun schedulePreAlarm(alarmId: Int, triggerTime: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.salman.bitclock.PRE_ALARM_TRIGGER"
            putExtra("ALARM_ID", alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarmId + 100000, intent, // Unique request code for pre-alarm
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, getMainActivityPendingIntent())
        alarmManager?.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun scheduleAlarmAtTime(alarmId: Int, triggerTime: Long, label: String) {
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null")
            return
        }

        if (!canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms. App needs permission.")
            return
        }

        Log.d(TAG, "Scheduling alarm ID: $alarmId for: $triggerTime")

        val pendingIntent = createPendingIntent(alarmId)
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, getMainActivityPendingIntent())

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancelAlarm(alarmId: Int) {
        if (alarmManager == null) return
        val pendingIntent = createPendingIntent(alarmId)
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Canceled alarm ID: $alarmId")
    }

    private fun createPendingIntent(alarmId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.salman.bitclock.ALARM_TRIGGER"
            putExtra("ALARM_ID", alarmId)
        }
        return PendingIntent.getBroadcast(
            context, alarmId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return PendingIntent.getActivity(
            context, -1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
