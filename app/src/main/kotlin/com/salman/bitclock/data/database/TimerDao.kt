package com.salman.bitclock.data.database

import androidx.room.*
import com.salman.bitclock.data.models.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer): Long

    @Update
    suspend fun update(timer: Timer)

    @Delete
    suspend fun delete(timer: Timer)

    @Query("SELECT * FROM timers")
    fun getAllTimers(): Flow<List<Timer>>

    @Query("SELECT * FROM timers")
    suspend fun getAllTimersSync(): List<Timer>
}
