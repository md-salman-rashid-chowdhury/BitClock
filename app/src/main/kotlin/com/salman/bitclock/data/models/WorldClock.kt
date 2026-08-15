package com.salman.bitclock.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_clocks")
data class WorldClock(
    @PrimaryKey
    val timeZoneId: String,
    val label: String
)
