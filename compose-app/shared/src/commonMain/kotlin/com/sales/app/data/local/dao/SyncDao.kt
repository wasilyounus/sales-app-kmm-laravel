package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.SyncTimestampEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {
    @Query("SELECT * FROM sync_timestamps WHERE key = :key")
    suspend fun getSyncTimestamp(key: String): SyncTimestampEntity?
    
    @Query("SELECT * FROM sync_timestamps")
    fun getAllSyncTimestamps(): Flow<List<SyncTimestampEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncTimestamp(timestamp: SyncTimestampEntity)
    
    @Update
    suspend fun updateSyncTimestamp(timestamp: SyncTimestampEntity)
    
    @Query("DELETE FROM sync_timestamps WHERE key = :key")
    suspend fun deleteSyncTimestamp(key: String)
    
    @Query("DELETE FROM sync_timestamps")
    suspend fun deleteAllSyncTimestamps()
}
