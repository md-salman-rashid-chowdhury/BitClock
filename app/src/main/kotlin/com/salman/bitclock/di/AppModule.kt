package com.salman.bitclock.di

import android.app.Application
import android.content.Context
import com.salman.bitclock.data.database.AlarmDao
import com.salman.bitclock.data.database.AppDatabase
import com.salman.bitclock.data.database.TimerDao
import com.salman.bitclock.data.database.WorldClockDao
import com.salman.bitclock.data.repository.AlarmRepository
import com.salman.bitclock.utils.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AlarmRepositoryEntryPoint {
        fun alarmRepository(): AlarmRepository
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase {
        return AppDatabase.getDatabase(application)
    }

    @Provides
    @Singleton
    fun provideAlarmDao(database: AppDatabase): AlarmDao = database.alarmDao()

    @Provides
    @Singleton
    fun provideTimerDao(database: AppDatabase): TimerDao = database.timerDao()

    @Provides
    @Singleton
    fun provideWorldClockDao(database: AppDatabase): WorldClockDao = database.worldClockDao()

    @Provides
    @Singleton
    fun provideSleepDao(database: AppDatabase): com.salman.bitclock.data.database.SleepDao = database.sleepDao()

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): com.salman.bitclock.data.database.HabitDao = database.habitDao()

    @Provides
    @Singleton
    fun provideProfileDao(database: AppDatabase): com.salman.bitclock.data.database.ProfileDao = database.profileDao()

    @Provides
    @Singleton
    fun provideAuditLogDao(database: AppDatabase): com.salman.bitclock.data.database.AuditLogDao = database.auditLogDao()

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }
}
