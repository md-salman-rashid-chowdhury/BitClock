package com.salman.bitclock.di;

import android.app.Application;
import android.content.Context;

import com.salman.bitclock.data.AlarmRepository;
import com.salman.bitclock.data.database.AlarmDao;
import com.salman.bitclock.data.database.AppDatabase;
import com.salman.bitclock.data.database.TimerDao;
import com.salman.bitclock.utils.AlarmScheduler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.EntryPoint;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    // This EntryPoint is correctly defined to allow access from non-Hilt components like BroadcastReceivers.
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    public interface AlarmRepositoryEntryPoint {
        AlarmRepository alarmRepository();
    }

    @Provides
    @Singleton
    public AppDatabase provideDatabase(Application application) {
        return AppDatabase.getDatabase(application);
    }

    @Provides
    @Singleton
    public AlarmDao provideAlarmDao(AppDatabase appDatabase) {
        return appDatabase.alarmDao();
    }

    @Provides
    @Singleton
    public TimerDao provideTimerDao(AppDatabase appDatabase) {
        return appDatabase.timerDao();
    }

    // **REMOVED**: provideAlarmRepository and provideTimerRepository are no longer needed
    // because Hilt can automatically create them using their @Inject constructors.

    @Provides
    @Singleton // **FIXED**: Ensures only one instance of AlarmScheduler is created.
    public AlarmScheduler provideAlarmScheduler(@ApplicationContext Context context) {
        return new AlarmScheduler(context);
    }
}
