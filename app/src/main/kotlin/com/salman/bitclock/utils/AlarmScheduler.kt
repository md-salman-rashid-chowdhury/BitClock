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
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null")
            return
        }

        if (!canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms. App needs permission.")
            return
        }

        val triggerTime = alarm.getNextAlarmTime()
        Log.d(TAG, "Scheduling alarm ID: ${alarm.id} for: $triggerTime")

        val pendingIntent = createPendingIntent(alarm.id)
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
