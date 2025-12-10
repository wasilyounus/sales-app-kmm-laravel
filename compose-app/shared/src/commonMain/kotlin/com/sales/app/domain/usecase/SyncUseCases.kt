package com.sales.app.domain.usecase

import com.sales.app.domain.model.SyncType
import com.sales.app.domain.repository.SyncRepository
import com.sales.app.util.Result

class SyncDataUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(companyId: Int, types: List<SyncType>): Result<Unit> {
        return syncRepository.sync(companyId, types)
    }
}

class SyncMasterDataUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return syncRepository.syncMasterData(companyId)
    }
}

class FullSyncUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return syncRepository.fullSync(companyId)
    }
}
