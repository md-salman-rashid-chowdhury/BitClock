package com.salman.bitclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.salman.bitclock.di.AppModule
import com.salman.bitclock.utils.AlarmScheduler
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val pendingResult = goAsync()
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext, AppModule.AlarmRepositoryEntryPoint::class.java
            )
            val repository = entryPoint.alarmRepository()
            val scheduler = AlarmScheduler(context)

            scope.launch {
                try {
                    val alarms = repository.getAllAlarmsSync()
                    for (alarm in alarms) {
                        if (alarm.isEnabled) {
                            scheduler.scheduleAlarm(alarm)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
