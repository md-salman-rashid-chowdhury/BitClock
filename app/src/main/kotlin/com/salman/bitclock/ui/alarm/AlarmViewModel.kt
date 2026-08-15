package com.salman.bitclock.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.models.Habit
import com.salman.bitclock.data.repository.AlarmRepository
import com.salman.bitclock.data.repository.HabitRepository
import com.salman.bitclock.data.repository.ProfileRepository
import com.salman.bitclock.utils.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler,
    private val habitRepository: HabitRepository,
    private val profileRepository: ProfileRepository,
    private val secureStorage: com.salman.bitclock.utils.SecureStorageManager
) : ViewModel() {

    fun getDefaultAccountabilityContact(): String {
        return secureStorage.getString("master_accountability_contact") ?: ""
    }

    val alarms: StateFlow<List<Alarm>> = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profiles = profileRepository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleAlarm(alarm: Alarm, isEnabled: Boolean) {
        viewModelScope.launch {
            val updatedAlarm = alarm.copy(isEnabled = isEnabled)
            repository.update(updatedAlarm)
            if (isEnabled) {
                scheduler.scheduleAlarm(updatedAlarm)
            } else {
                scheduler.cancelAlarm(updatedAlarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            scheduler.cancelAlarm(alarm.id)
            repository.delete(alarm)
            lastDeletedAlarm = alarm
        }
    }

    private var lastDeletedAlarm: Alarm? = null

    fun undoDelete() {
        lastDeletedAlarm?.let {
            viewModelScope.launch {
                repository.insert(it)
                if (it.isEnabled) {
                    scheduler.scheduleAlarm(it)
                }
                lastDeletedAlarm = null
            }
        }
    }

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val id = repository.insert(alarm).toInt()
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm.copy(id = id))
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.update(alarm)
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm)
            } else {
                scheduler.cancelAlarm(alarm.id)
            }
        }
    }

    fun addProfile(name: String) {
        viewModelScope.launch {
            profileRepository.insertProfile(com.salman.bitclock.data.models.AlarmProfile(name = name))
        }
    }

    fun deleteProfile(profile: com.salman.bitclock.data.models.AlarmProfile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }

    fun toggleProfile(profile: com.salman.bitclock.data.models.AlarmProfile, isActive: Boolean) {
        viewModelScope.launch {
            profileRepository.updateProfile(profile.copy(isActive = isActive))
            // Enable/Disable alarms associated with this profile
            val allAlarms = repository.getAllAlarmsSync()
            allAlarms.filter { it.profileId == profile.id }.forEach { alarm ->
                toggleAlarm(alarm, isActive)
            }
        }
    }

    fun getHabitsForAlarm(alarmId: Int) = habitRepository.getHabitsForAlarm(alarmId)

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.insertHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }
}
