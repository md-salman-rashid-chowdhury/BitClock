package com.salman.bitclock.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.salman.bitclock.R
import com.salman.bitclock.data.repository.AlarmRepository
import com.salman.bitclock.di.AppModule
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmWidgetProvider : AppWidgetProvider() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext, AppModule.AlarmRepositoryEntryPoint::class.java
        )
        val repository = entryPoint.alarmRepository()

        scope.launch {
            val alarms = repository.getAllAlarmsSync()
            val nextAlarm = alarms.filter { it.isEnabled }
                .map { it.getNextAlarmTime() }
                .minOrNull()

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, nextAlarm)
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, nextAlarmTime: Long?) {
        val views = RemoteViews(context.packageName, R.layout.alarm_widget)
        
        if (nextAlarmTime != null) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            views.setTextViewText(R.id.widget_alarm_time, sdf.format(Date(nextAlarmTime)))
        } else {
            views.setTextViewText(R.id.widget_alarm_time, "--:--")
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
