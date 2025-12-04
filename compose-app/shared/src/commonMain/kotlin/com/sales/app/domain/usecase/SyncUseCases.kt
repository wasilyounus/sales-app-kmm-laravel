package com.sales.app.domain.usecase

import com.sales.app.domain.repository.SyncRepository
import com.sales.app.util.Result

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
