package com.salman.bitclock.di;

import android.app.Application;
import android.content.Context;

import com.salman.bitclock.data.repository.AlarmRepository;
import com.salman.bitclock.data.AppStateManager;
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

/**
 * Dependency Injection module using Hilt.
 * Provides singleton instances for the app's core components.
 */
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    /**
     * EntryPoint for accessing AlarmRepository in non-Hilt classes (e.g., BroadcastReceivers).
     */
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

    @Provides
    @Singleton
    public AppStateManager provideAppStateManager() {
        return new AppStateManager();
    }

    @Provides
    @Singleton
    public AlarmScheduler provideAlarmScheduler(@ApplicationContext Context context) {
        return new AlarmScheduler(context);
    }
}
