package com.salman.bitclock.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = Alarm::class,
            parentColumns = ["id"],
            childColumns = ["alarm_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "alarm_id")
    val alarmId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
