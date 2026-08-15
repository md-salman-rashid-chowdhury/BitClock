package com.salman.bitclock.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.salman.bitclock.data.models.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(log: AuditLog)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<AuditLog>>
}
