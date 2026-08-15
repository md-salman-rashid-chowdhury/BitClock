package com.salman.bitclock.ui.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.data.models.Timer
import com.salman.bitclock.data.repository.TimerRepository
import com.salman.bitclock.services.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: TimerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val timers: StateFlow<List<Timer>> = repository.getAllTimers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTimer(name: String, durationMs: Long) {
        viewModelScope.launch {
            val timer = Timer(
                name = if (name.isEmpty()) "Timer" else name,
                initialDurationMs = durationMs,
                remainingMs = durationMs
            )
            repository.insert(timer)
        }
    }

    fun startTimer(timerId: Int) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_TIMER_ID, timerId)
        }
        context.startService(intent)
    }

    fun pauseTimer(timerId: Int) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
            putExtra(TimerService.EXTRA_TIMER_ID, timerId)
        }
        context.startService(intent)
    }

    fun resetTimer(timerId: Int) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_RESET
            putExtra(TimerService.EXTRA_TIMER_ID, timerId)
        }
        context.startService(intent)
    }

    fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            repository.delete(timer)
            // Also ensure it's stopped in service
            val intent = Intent(context, TimerService::class.java).apply {
                action = TimerService.ACTION_STOP
                putExtra(TimerService.EXTRA_TIMER_ID, timer.id)
            }
            context.startService(intent)
        }
    }
}
