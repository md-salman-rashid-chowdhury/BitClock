package com.salman.bitclock.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    @ColumnInfo(name = "end_time")
    val endTime: Long,
    @ColumnInfo(name = "movement_data")
    val movementData: String, // Serialized list of movement intensities
    @ColumnInfo(name = "wake_quality_score")
    val wakeQualityScore: Int
)
