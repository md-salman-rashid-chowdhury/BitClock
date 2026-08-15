package com.salman.bitclock.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.data.models.SleepSession
import com.salman.bitclock.data.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val repository: SleepRepository
) : ViewModel() {

    val sleepSessions: StateFlow<List<SleepSession>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
