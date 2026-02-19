package com.salman.bitclock.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.data.models.Timer;

@Database(entities = {Alarm.class, Timer.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Access to Alarm data
    public abstract AlarmDao alarmDao();
    
    // Access to Timer data
    public abstract TimerDao timerDao();

    private static volatile AppDatabase INSTANCE;

    // Singleton pattern for database instance
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "bitclock_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
