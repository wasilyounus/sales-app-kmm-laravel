package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.GrnItemRequest
import com.sales.app.domain.model.Grn
import com.sales.app.domain.repository.GrnRepository
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetGrnsUseCase(
    private val repository: GrnRepository
) {
    operator fun invoke(accountId: Int): Flow<List<Grn>> {
        return repository.getGrnsByAccount(accountId)
    }
}

class GetGrnsByPurchaseUseCase(
    private val repository: GrnRepository
) {
    operator fun invoke(purchaseId: Int): Flow<List<Grn>> {
        return repository.getGrnsByPurchase(purchaseId)
    }
}

class GetGrnByIdUseCase(
    private val repository: GrnRepository
) {
    operator fun invoke(id: Int): Flow<Grn?> {
        return repository.getGrnById(id)
    }
}

class SyncGrnsUseCase(
    private val repository: GrnRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Unit> {
        return repository.syncGrns(accountId)
    }
}

class CreateGrnUseCase(
    private val repository: GrnRepository
) {
    suspend operator fun invoke(
        purchaseId: Int,
        date: String,
        vehicleNo: String?,
        invoiceNo: String?,
        notes: String?,
        items: List<GrnItemRequest>,
        accountId: Int
    ): Result<Grn> {
        return repository.createGrn(purchaseId, date, vehicleNo, invoiceNo, notes, items, accountId)
    }
}

class DeleteGrnUseCase(
    private val repository: GrnRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deleteGrn(id)
    }
}
