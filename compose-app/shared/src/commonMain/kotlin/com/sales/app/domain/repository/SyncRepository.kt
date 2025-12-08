package com.sales.app.domain.repository

import com.sales.app.data.local.entity.SyncTimestampEntity
import com.sales.app.domain.model.SyncType
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    suspend fun sync(accountId: Int, types: List<SyncType>): Result<Unit>
    suspend fun syncMasterData(accountId: Int): Result<Unit>
    suspend fun fullSync(accountId: Int): Result<Unit>
    fun getSyncStatus(): Flow<SyncTimestampEntity?>
}
