package com.sales.app.domain.usecase

import com.sales.app.domain.model.SyncType
import com.sales.app.domain.repository.SyncRepository
import com.sales.app.util.Result

class SyncDataUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(accountId: Int, types: List<SyncType>): Result<Unit> {
        return syncRepository.sync(accountId, types)
    }
}

class SyncMasterDataUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Unit> {
        return syncRepository.syncMasterData(accountId)
    }
}

class FullSyncUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Unit> {
        return syncRepository.fullSync(accountId)
    }
}
