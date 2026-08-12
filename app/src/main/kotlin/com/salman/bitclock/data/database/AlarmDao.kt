package com.salman.bitclock.data.database

import androidx.room.*
import com.salman.bitclock.data.models.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: Alarm): Long

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("SELECT * FROM alarms ORDER BY hour, minute ASC")
    fun getAllAlarms(): Flow<List<Alarm>>
    
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    fun getAlarmById(alarmId: Int): Flow<Alarm?>

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarmsSync(): List<Alarm>

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmByIdSync(alarmId: Int): Alarm?
}
