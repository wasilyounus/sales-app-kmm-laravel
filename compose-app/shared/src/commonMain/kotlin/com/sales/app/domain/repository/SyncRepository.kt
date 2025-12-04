package com.sales.app.domain.repository

import com.sales.app.data.local.entity.SyncTimestampEntity
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    suspend fun syncMasterData(accountId: Int): Result<Unit>
    suspend fun fullSync(accountId: Int): Result<Unit>
    fun getSyncStatus(): Flow<SyncTimestampEntity?>
}
