package com.salman.bitclock.data

import com.google.gson.Gson
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.models.AlarmProfile
import com.salman.bitclock.data.models.Habit
import com.salman.bitclock.data.repository.AlarmRepository
import com.salman.bitclock.data.repository.HabitRepository
import com.salman.bitclock.data.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val database: com.salman.bitclock.data.database.AppDatabase,
    private val alarmRepository: AlarmRepository,
    private val habitRepository: HabitRepository,
    private val profileRepository: ProfileRepository,
    private val gson: Gson
) {
    data class BackupData(
        val alarms: List<Alarm>,
        val habits: List<Habit>,
        val profiles: List<AlarmProfile>
    )

    suspend fun exportData(): String {
        val data = BackupData(
            alarms = alarmRepository.getAllAlarmsSync(),
            habits = habitRepository.getAllHabitsSync(),
            profiles = profileRepository.getAllProfilesSync()
        )
        return gson.toJson(data)
    }

    suspend fun importData(json: String) {
        val data = try {
            gson.fromJson(json, BackupData::class.java)
        } catch (e: Exception) {
            null
        } ?: return
        
        // Clear existing data for a clean restore
        database.clearAllTables()
        
        // Insert profiles first (FK dependency)
        data.profiles?.forEach { profileRepository.insertProfile(it) }
        
        // Insert alarms
        data.alarms?.forEach { alarmRepository.insert(it) }
        
        // Insert habits
        data.habits?.forEach { habitRepository.insertHabit(it) }
    }
}
