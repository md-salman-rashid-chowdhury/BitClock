package com.salman.bitclock.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "initial_duration")
    val initialDurationMs: Long,

    @ColumnInfo(name = "remaining_time")
    val remainingMs: Long,

    @ColumnInfo(name = "status")
    val status: Int = 0 // 0 = stopped, 1 = running
)
