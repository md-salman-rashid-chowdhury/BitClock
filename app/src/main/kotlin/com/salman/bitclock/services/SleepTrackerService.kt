package com.salman.bitclock.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.salman.bitclock.data.models.SleepSession
import com.salman.bitclock.data.repository.AlarmRepository
import com.salman.bitclock.data.repository.SleepRepository
import com.salman.bitclock.utils.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
class SleepTrackerService : Service(), SensorEventListener {

    @Inject
    lateinit var sleepRepository: SleepRepository

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var startTime: Long = 0
    private var movementData = mutableListOf<Pair<Long, Float>>()
    
    private val CHANNEL_ID = "sleep_tracker_channel"
    private val NOTIFICATION_ID = 2

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        startForeground(NOTIFICATION_ID, createNotification())
        
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            val magnitude = sqrt(x * x + y * y + z * z)
            val delta = Math.abs(magnitude - SensorManager.GRAVITY_EARTH)
            
            if (delta > 0.5f) {
                movementData.add(System.currentTimeMillis() to delta)
            }
            
            checkSmartWake()
        }
    }

    private fun checkSmartWake() {
        serviceScope.launch {
            val alarms = alarmRepository.getAllAlarmsSync()
            val now = System.currentTimeMillis()
            
            for (alarm in alarms) {
                if (alarm.isEnabled && alarm.smartWakeEnabled) {
                    val nextAlarmTime = alarm.getNextAlarmTime()
                    val windowStart = nextAlarmTime - TimeUnit.MINUTES.toMillis(alarm.smartWakeWindowMinutes.toLong())
                    
                    if (now in windowStart..nextAlarmTime) {
                        // If light sleep detected (recent movement)
                        if (isLightSleepDetected()) {
                            triggerAlarmEarly(alarm.id)
                        }
                    }
                }
            }
        }
    }

    private fun isLightSleepDetected(): Boolean {
        val window = 5 * 60 * 1000 // 5 minutes
        val recentMovements = movementData.filter { it.first > System.currentTimeMillis() - window }
        return recentMovements.size > 5 // Arbitrary threshold for light sleep
    }

    private fun triggerAlarmEarly(alarmId: Int) {
        alarmScheduler.scheduleAlarmAtTime(alarmId, System.currentTimeMillis(), "Smart Wake")
        stopSelf()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        saveSleepSession()
        serviceScope.cancel()
    }

    private fun saveSleepSession() {
        val endTime = System.currentTimeMillis()
        serviceScope.launch {
            val session = SleepSession(
                startTime = startTime,
                endTime = endTime,
                movementData = movementData.joinToString(";") { "${it.first},${it.second}" },
                wakeQualityScore = calculateWakeQuality()
            )
            sleepRepository.insertSession(session)
        }
    }

    private fun calculateWakeQuality(): Int {
        return 85
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sleep Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sleep Tracking Active")
            .setContentText("Monitoring for light sleep...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
