package com.salman.bitclock.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.salman.bitclock.R
import com.salman.bitclock.data.models.Timer
import com.salman.bitclock.data.repository.TimerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var timerRepository: TimerRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var updateJob: Job? = null

    private val runningTimers = mutableMapOf<Int, Timer>()

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_RESET = "ACTION_RESET"
        const val EXTRA_TIMER_ID = "EXTRA_TIMER_ID"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "TIMER_CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val timerId = intent?.getIntExtra(EXTRA_TIMER_ID, -1) ?: -1

        if (timerId != -1) {
            when (action) {
                ACTION_START -> startTimer(timerId)
                ACTION_PAUSE -> pauseTimer(timerId)
                ACTION_STOP -> stopTimer(timerId)
                ACTION_RESET -> resetTimer(timerId)
            }
        }

        return START_STICKY
    }

    private fun startTimer(timerId: Int) {
        serviceScope.launch {
            val timers = timerRepository.getAllTimersSync()
            val timer = timers.find { it.id == timerId } ?: return@launch
            
            val updatedTimer = timer.copy(
                status = 1,
                endTime = System.currentTimeMillis() + timer.remainingMs
            )
            timerRepository.update(updatedTimer)
            runningTimers[timerId] = updatedTimer
            
            startForegroundServiceIfNeeded()
            ensureUpdateLoop()
        }
    }

    private fun pauseTimer(timerId: Int) {
        serviceScope.launch {
            val timer = runningTimers[timerId] ?: return@launch
            val now = System.currentTimeMillis()
            val remaining = (timer.endTime - now).coerceAtLeast(0)
            
            val updatedTimer = timer.copy(status = 0, remainingMs = remaining, endTime = 0)
            timerRepository.update(updatedTimer)
            runningTimers.remove(timerId)
            
            checkToStopService()
        }
    }

    private fun stopTimer(timerId: Int) {
        serviceScope.launch {
            val timers = timerRepository.getAllTimersSync()
            val timer = timers.find { it.id == timerId } ?: return@launch
            
            val updatedTimer = timer.copy(status = 0, remainingMs = timer.initialDurationMs, endTime = 0)
            timerRepository.update(updatedTimer)
            runningTimers.remove(timerId)
            
            checkToStopService()
        }
    }

    private fun resetTimer(timerId: Int) {
        stopTimer(timerId)
    }

    private fun startForegroundServiceIfNeeded() {
        val notification = createNotification("Timer running")
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun ensureUpdateLoop() {
        if (updateJob != null && updateJob?.isActive == true) return

        updateJob = serviceScope.launch {
            while (runningTimers.isNotEmpty()) {
                updateNotification()
                delay(1000)
                
                // Check if any timer finished
                val now = System.currentTimeMillis()
                val finished = runningTimers.filter { it.value.endTime <= now }
                finished.forEach { (id, _) ->
                    onTimerFinished(id)
                }
            }
            stopForeground(true)
            stopSelf()
        }
    }

    private fun onTimerFinished(timerId: Int) {
        runningTimers.remove(timerId)
        serviceScope.launch {
            val timers = timerRepository.getAllTimersSync()
            val timer = timers.find { it.id == timerId } ?: return@launch
            timerRepository.update(timer.copy(status = 0, remainingMs = 0, endTime = 0))
            
            // TODO: Trigger alarm/sound for timer finish
            showFinishedNotification(timer.name)
        }
    }

    private fun showFinishedNotification(timerName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Finished")
            .setContentText(timerName)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(timerIdToNotificationId(timerName), notification)
    }

    private fun timerIdToNotificationId(name: String) = name.hashCode()

    private fun updateNotification() {
        val notification = createNotification(getRunningTimersSummary())
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getRunningTimersSummary(): String {
        if (runningTimers.isEmpty()) return "No active timers"
        val now = System.currentTimeMillis()
        return runningTimers.values.joinToString("\n") { timer ->
            val remaining = (timer.endTime - now).coerceAtLeast(0)
            "${timer.name}: ${formatTime(remaining)}"
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BitClock Timer")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun checkToStopService() {
        if (runningTimers.isEmpty()) {
            updateJob?.cancel()
            stopForeground(true)
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Timers", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
