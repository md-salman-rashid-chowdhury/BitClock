package com.salman.bitclock.data.repository

import com.salman.bitclock.data.AppStateManager
import com.salman.bitclock.data.database.TimerDao
import com.salman.bitclock.data.models.Timer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepository @Inject constructor(
    private val timerDao: TimerDao,
    private val stateManager: AppStateManager
) {
    private val TIMERS_KEY = "timers_list"

    fun getAllTimers(): Flow<List<Timer>> = timerDao.getAllTimers()

    suspend fun getAllTimersCached(): List<Timer> {
        @Suppress("UNCHECKED_CAST")
        var timers = stateManager.getData(TIMERS_KEY) as? List<Timer>
        if (timers == null) {
            timers = timerDao.getAllTimersSync()
            stateManager.saveData(TIMERS_KEY, timers)
        }
        return timers
    }

    suspend fun insert(timer: Timer): Long {
        val id = timerDao.insert(timer)
        stateManager.removeData(TIMERS_KEY)
        return id
    }

    suspend fun update(timer: Timer) {
        timerDao.update(timer)
        stateManager.removeData(TIMERS_KEY)
    }

    suspend fun delete(timer: Timer) {
        timerDao.delete(timer)
        stateManager.removeData(TIMERS_KEY)
    }
}
