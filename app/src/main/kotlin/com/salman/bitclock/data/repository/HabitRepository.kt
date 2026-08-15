package com.salman.bitclock.data.repository

import com.salman.bitclock.data.database.HabitDao
import com.salman.bitclock.data.models.Habit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao
) {
    fun getHabitsForAlarm(alarmId: Int): Flow<List<Habit>> = habitDao.getHabitsForAlarm(alarmId)

    suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)
}
