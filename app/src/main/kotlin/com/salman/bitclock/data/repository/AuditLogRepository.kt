package com.salman.bitclock.data.repository

import com.salman.bitclock.data.database.AuditLogDao
import com.salman.bitclock.data.models.AuditLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuditLogRepository @Inject constructor(
    private val auditLogDao: AuditLogDao
) {
    fun getRecentLogs(): Flow<List<AuditLog>> = auditLogDao.getRecentLogs()

    suspend fun insertLog(action: String, details: String) {
        auditLogDao.insert(AuditLog(action = action, details = details))
    }
}
