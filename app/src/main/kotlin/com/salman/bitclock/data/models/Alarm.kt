package com.salman.bitclock.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

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
    val repeatDays: Int = 0, // Bitmask for repeating days
    
    @ColumnInfo(name = "snooze_duration")
    val snoozeMinutes: Int = 10,
    
    @ColumnInfo(name = "sound_uri")
    val soundUri: String = "",
    
    @ColumnInfo(name = "vibrate")
    val isVibrate: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isRepeating(): Boolean = repeatDays != 0

    fun getNextAlarmTime(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        if (!isRepeating()) {
            return calendar.timeInMillis
        }

        for (i in 0 until 7) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // SUNDAY = 1, MONDAY = 2, ...
            val dayBit = 1 shl ((dayOfWeek + 5) % 7) // Convert to Mon-Sun bitmask

            if ((repeatDays and dayBit) != 0) {
                return calendar.timeInMillis
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return 0 // Should not happen
    }
}
