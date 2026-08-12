package com.salman.bitclock.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.data.models.Alarm
import com.salman.bitclock.data.repository.AlarmRepository
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
    private val scheduler: AlarmScheduler
) : ViewModel() {

    val alarms: StateFlow<List<Alarm>> = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleAlarm(alarm: Alarm, enabled: Boolean) {
        viewModelScope.launch {
            val updatedAlarm = alarm.copy(isEnabled = enabled)
            repository.update(updatedAlarm)
            if (enabled) {
                scheduler.scheduleAlarm(updatedAlarm)
            } else {
                scheduler.cancelAlarm(updatedAlarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.delete(alarm)
            scheduler.cancelAlarm(alarm.id)
        }
    }

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val id = repository.insert(alarm)
            scheduler.scheduleAlarm(alarm.copy(id = id.toInt()))
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
}
