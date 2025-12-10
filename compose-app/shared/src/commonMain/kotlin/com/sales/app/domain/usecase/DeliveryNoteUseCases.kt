package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.DeliveryNoteItemRequest
import com.sales.app.domain.model.DeliveryNote
import com.sales.app.domain.repository.DeliveryNoteRepository
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetDeliveryNotesUseCase(
    private val repository: DeliveryNoteRepository
) {
    operator fun invoke(companyId: Int): Flow<List<DeliveryNote>> {
        return repository.getDeliveryNotesByAccount(companyId)
    }
}

class GetDeliveryNotesBySaleUseCase(
    private val repository: DeliveryNoteRepository
) {
    operator fun invoke(saleId: Int): Flow<List<DeliveryNote>> {
        return repository.getDeliveryNotesBySale(saleId)
    }
}

class GetDeliveryNoteByIdUseCase(
    private val repository: DeliveryNoteRepository
) {
    operator fun invoke(id: Int): Flow<DeliveryNote?> {
        return repository.getDeliveryNoteById(id)
    }
}

class SyncDeliveryNotesUseCase(
    private val repository: DeliveryNoteRepository
) {
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return repository.syncDeliveryNotes(companyId)
    }
}

class CreateDeliveryNoteUseCase(
    private val repository: DeliveryNoteRepository
) {
    suspend operator fun invoke(
        saleId: Int,
        date: String,
        vehicleNo: String?,
        lrNo: String?,
        notes: String?,
        items: List<DeliveryNoteItemRequest>,
        companyId: Int
    ): Result<DeliveryNote> {
        return repository.createDeliveryNote(saleId, date, vehicleNo, lrNo, notes, items, companyId)
    }
}

class DeleteDeliveryNoteUseCase(
    private val repository: DeliveryNoteRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deleteDeliveryNote(id)
    }
}
