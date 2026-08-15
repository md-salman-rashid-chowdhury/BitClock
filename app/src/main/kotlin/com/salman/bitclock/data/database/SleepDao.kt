package com.salman.bitclock.data.database

import androidx.room.*
import com.salman.bitclock.data.models.SleepSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SleepSession): Long

    @Query("SELECT * FROM sleep_sessions ORDER BY start_time DESC")
    fun getAllSessions(): Flow<List<SleepSession>>

    @Query("SELECT * FROM sleep_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): SleepSession?

    @Delete
    suspend fun delete(session: SleepSession)
}
