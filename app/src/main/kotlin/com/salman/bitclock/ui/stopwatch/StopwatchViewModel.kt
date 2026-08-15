package com.salman.bitclock.ui.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.data.models.Lap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _laps = MutableStateFlow<List<Lap>>(emptyList())
    val laps = _laps.asStateFlow()

    private var stopwatchJob: Job? = null
    private var lastTickTime = 0L

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        lastTickTime = System.currentTimeMillis()
        stopwatchJob = viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                _elapsedTime.value += (now - lastTickTime)
                lastTickTime = now
                delay(10)
            }
        }
    }

    fun pause() {
        _isRunning.value = false
        stopwatchJob?.cancel()
    }

    fun lap() {
        val currentElapsed = _elapsedTime.value
        val lapNumber = _laps.value.size + 1
        val lastLapTime = _laps.value.lastOrNull()?.overallTime ?: 0L
        val lapTime = currentElapsed - lastLapTime
        
        val newLap = Lap(lapNumber, lapTime, currentElapsed)
        _laps.value = listOf(newLap) + _laps.value
    }

    fun reset() {
        pause()
        _elapsedTime.value = 0L
        _laps.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        stopwatchJob?.cancel()
    }
}
