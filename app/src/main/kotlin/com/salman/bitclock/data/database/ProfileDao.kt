package com.salman.bitclock.data.database

import androidx.room.*
import com.salman.bitclock.data.models.AlarmProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: AlarmProfile): Long

    @Update
    suspend fun update(profile: AlarmProfile)

    @Delete
    suspend fun delete(profile: AlarmProfile)

    @Query("SELECT * FROM alarm_profiles")
    fun getAllProfiles(): Flow<List<AlarmProfile>>

    @Query("SELECT * FROM alarm_profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: Int): AlarmProfile?

    @Query("SELECT * FROM alarm_profiles")
    suspend fun getAllProfilesSync(): List<AlarmProfile>
}
