package com.salman.bitclock.data.database

import androidx.room.TypeConverter
import com.salman.bitclock.data.models.MissionType

class Converters {
    @TypeConverter
    fun fromMissionType(value: MissionType): String {
        return value.name
    }

    @TypeConverter
    fun toMissionType(value: String): MissionType {
        return try {
            MissionType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MissionType.NONE
        }
    }
}
