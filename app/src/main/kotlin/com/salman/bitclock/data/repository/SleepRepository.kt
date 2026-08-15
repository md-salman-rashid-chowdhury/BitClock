package com.salman.bitclock.data.repository

import com.salman.bitclock.data.database.SleepDao
import com.salman.bitclock.data.models.SleepSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepRepository @Inject constructor(
    private val sleepDao: SleepDao
) {
    fun getAllSessions(): Flow<List<SleepSession>> = sleepDao.getAllSessions()

    suspend fun insertSession(session: SleepSession): Long = sleepDao.insert(session)

    suspend fun getSessionById(sessionId: Long): SleepSession? = sleepDao.getSessionById(sessionId)

    suspend fun deleteSession(session: SleepSession) = sleepDao.delete(session)
}
