package com.salman.bitclock.data.database

import androidx.room.*
import com.salman.bitclock.data.models.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits WHERE alarm_id = :alarmId")
    fun getHabitsForAlarm(alarmId: Int): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE alarm_id = :alarmId")
    suspend fun getHabitsForAlarmSync(alarmId: Int): List<Habit>
}
