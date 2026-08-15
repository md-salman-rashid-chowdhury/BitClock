package com.salman.bitclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.salman.bitclock.di.AppModule
import com.salman.bitclock.utils.AlarmScheduler
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "BootCompletedReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "Received broadcast: $action")
        
        val bootActions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )

        if (action in bootActions) {
            val pendingResult = goAsync()
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext, AppModule.AlarmRepositoryEntryPoint::class.java
            )
            val repository = entryPoint.alarmRepository()
            val scheduler = AlarmScheduler(context)

            scope.launch {
                try {
                    val alarms = repository.getAllAlarmsSync()
                    Log.d(TAG, "Rescheduling ${alarms.count { it.isEnabled }} enabled alarms")
                    for (alarm in alarms) {
                        if (alarm.isEnabled) {
                            scheduler.scheduleAlarm(alarm)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error rescheduling alarms after boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
