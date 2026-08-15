package com.salman.bitclock.data.database

import androidx.room.*
import com.salman.bitclock.data.models.WorldClock
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldClockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worldClock: WorldClock)

    @Delete
    suspend fun delete(worldClock: WorldClock)

    @Query("SELECT * FROM world_clocks")
    fun getAllWorldClocks(): Flow<List<WorldClock>>
}
