package com.salman.bitclock.data.repository

import com.salman.bitclock.data.database.ProfileDao
import com.salman.bitclock.data.models.AlarmProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao
) {
    fun getAllProfiles(): Flow<List<AlarmProfile>> = profileDao.getAllProfiles()

    suspend fun getAllProfilesSync(): List<AlarmProfile> = profileDao.getAllProfilesSync()

    suspend fun insertProfile(profile: AlarmProfile) = profileDao.insert(profile)

    suspend fun updateProfile(profile: AlarmProfile) = profileDao.update(profile)

    suspend fun deleteProfile(profile: AlarmProfile) = profileDao.delete(profile)
}
