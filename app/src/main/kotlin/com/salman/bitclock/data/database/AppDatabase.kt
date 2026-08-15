package com.salman.bitclock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.models.Timer
import com.salman.bitclock.data.models.WorldClock

@Database(entities = [Alarm::class, Timer::class, WorldClock::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun timerDao(): TimerDao
    abstract fun worldClockDao(): WorldClockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE alarms ADD COLUMN mission_type TEXT NOT NULL DEFAULT 'NONE'")
                db.execSQL("ALTER TABLE alarms ADD COLUMN mission_difficulty INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE alarms ADD COLUMN mission_target TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE alarms ADD COLUMN snooze_limit INTEGER NOT NULL DEFAULT 3")
                db.execSQL("ALTER TABLE alarms ADD COLUMN snooze_count INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bitclock_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
