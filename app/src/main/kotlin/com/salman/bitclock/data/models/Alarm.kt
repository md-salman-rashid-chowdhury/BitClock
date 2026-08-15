package com.salman.bitclock.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneId

enum class MissionType {
    NONE, MATH, SHAKE, BARCODE
}

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "hour")
    val hour: Int,

    @ColumnInfo(name = "minute")
    val minute: Int,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "label")
    val label: String = "",

    @ColumnInfo(name = "repeat_days")
    val repeatDays: Int = 0, // Bitmask for repeating days (Mon=1, Tue=2, ..., Sun=64)

    @ColumnInfo(name = "snooze_duration")
    val snoozeMinutes: Int = 10,

    @ColumnInfo(name = "sound_uri")
    val soundUri: String = "",

    @ColumnInfo(name = "vibrate")
    val isVibrate: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "mission_type")
    val missionType: MissionType = MissionType.NONE,

    @ColumnInfo(name = "mission_difficulty")
    val missionDifficulty: Int = 1,

    @ColumnInfo(name = "mission_target")
    val missionTarget: String = "",

    @ColumnInfo(name = "snooze_limit")
    val snoozeLimit: Int = 3, // 0 for infinite, -1 for blocked

    @ColumnInfo(name = "snooze_count")
    val snoozeCount: Int = 0,

    @ColumnInfo(name = "smart_wake_enabled")
    val smartWakeEnabled: Boolean = false,

    @ColumnInfo(name = "smart_wake_window")
    val smartWakeWindowMinutes: Int = 20,

    @ColumnInfo(name = "has_habit_checklist")
    val hasHabitChecklist: Boolean = false,

    @ColumnInfo(name = "profile_id")
    val profileId: Int? = null,

    @ColumnInfo(name = "accountability_contact")
    val accountabilityContact: String? = null,

    @ColumnInfo(name = "accountability_delay")
    val accountabilityDelayMinutes: Int = 10,

    @ColumnInfo(name = "pre_alarm_enabled")
    val preAlarmEnabled: Boolean = false,

    @ColumnInfo(name = "pre_alarm_minutes")
    val preAlarmMinutes: Int = 5,

    @ColumnInfo(name = "adaptive_difficulty")
    val adaptiveDifficultyEnabled: Boolean = false,

    @ColumnInfo(name = "dismissal_history_count")
    val dismissalHistoryCount: Int = 0,

    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis()
) {
    fun isRepeating(): Boolean = repeatDays != 0

    /**
     * Calculates the next trigger time for this alarm in milliseconds.
     * Uses java.time APIs for DST-safe calculation.
     */
    fun getNextAlarmTime(): Long {
        val now = LocalDateTime.now()
        var scheduledTime = now.withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        // If the scheduled time is in the past, move to tomorrow
        if (scheduledTime.isBefore(now) || scheduledTime.isEqual(now)) {
            scheduledTime = scheduledTime.plusDays(1)
        }

        if (!isRepeating()) {
            return scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // For repeating alarms, find the next active day
        for (i in 0 until 7) {
            // DayOfWeek is 1 (Monday) to 7 (Sunday)
            val dayOfWeek = scheduledTime.dayOfWeek.value
            val dayBit = 1 shl (dayOfWeek - 1)

            if ((repeatDays and dayBit) != 0) {
                return scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
            scheduledTime = scheduledTime.plusDays(1)
        }

        // Fallback to the first occurrence if something goes wrong
        return scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
