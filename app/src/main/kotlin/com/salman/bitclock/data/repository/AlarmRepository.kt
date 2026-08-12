package com.salman.bitclock.data.repository

import com.salman.bitclock.data.AppStateManager
import com.salman.bitclock.data.database.AlarmDao
import com.salman.bitclock.data.models.Alarm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao,
    private val stateManager: AppStateManager
) {
    private val ALARMS_KEY = "alarms_list"

    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()

    fun getAlarmById(id: Int): Flow<Alarm?> = alarmDao.getAlarmById(id)

    suspend fun getAllAlarmsSync(): List<Alarm> {
        @Suppress("UNCHECKED_CAST")
        var alarms = stateManager.getData(ALARMS_KEY) as? List<Alarm>
        if (alarms == null) {
            alarms = alarmDao.getAllAlarmsSync()
            stateManager.saveData(ALARMS_KEY, alarms)
        }
        return alarms
    }

    suspend fun insert(alarm: Alarm): Long {
        val id = alarmDao.insert(alarm)
        stateManager.removeData(ALARMS_KEY)
        return id
    }

    suspend fun update(alarm: Alarm) {
        alarmDao.update(alarm)
        stateManager.removeData(ALARMS_KEY)
    }

    suspend fun delete(alarm: Alarm) {
        alarmDao.delete(alarm)
        stateManager.removeData(ALARMS_KEY)
    }

    suspend fun getAlarmByIdSync(id: Int): Alarm? = alarmDao.getAlarmByIdSync(id)
}
