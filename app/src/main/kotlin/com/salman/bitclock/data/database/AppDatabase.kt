package com.salman.bitclock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.models.Timer

@Database(entities = [Alarm::class, Timer::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun timerDao(): TimerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bitclock_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
