package com.salman.bitclock.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AccountabilityWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val contact = inputData.getString("CONTACT") ?: return Result.failure()
        val alarmLabel = inputData.getString("LABEL") ?: "Alarm"

        sendAccountabilityAlert(contact, alarmLabel)
        
        return Result.success()
    }

    private fun sendAccountabilityAlert(contact: String, label: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "accountability_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Accountability Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Accountability Alert!")
            .setContentText("User failed to dismiss alarm '$label'. Notifying $contact...")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(3, notification)
        
        // In a real app, this is where you'd trigger an SMS or an API call to a backend
        // that handles the actual notification to the contact.
    }
}
