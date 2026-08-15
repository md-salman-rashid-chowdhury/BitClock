package com.salman.bitclock.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.data.database.WorldClockDao
import com.salman.bitclock.data.models.WorldClock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val worldClockDao: WorldClockDao
) : ViewModel() {

    val worldClocks: StateFlow<List<WorldClock>> = worldClockDao.getAllWorldClocks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWorldClock(timeZoneId: String, label: String) {
        viewModelScope.launch {
            worldClockDao.insert(WorldClock(timeZoneId, label))
        }
    }

    fun removeWorldClock(worldClock: WorldClock) {
        viewModelScope.launch {
            worldClockDao.delete(worldClock)
        }
    }
}
