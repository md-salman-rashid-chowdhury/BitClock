package com.salman.bitclock.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.data.models.Timer; // Assuming this is the correct model location

@Database(entities = {Alarm.class, Timer.class}, version = 1, exportSchema = false)
public abstract class

AppDatabase extends RoomDatabase {

    public abstract AlarmDao alarmDao();
    public abstract TimerDao timerDao(); // **FIX**: Added the missing TimerDao abstract method

    private static volatile AppDatabase INSTANCE;

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
