package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.DeliveryNoteItemRequest
import com.sales.app.domain.model.DeliveryNote
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface DeliveryNoteRepository {
    fun getDeliveryNotesByAccount(accountId: Int): Flow<List<DeliveryNote>>
    fun getDeliveryNotesBySale(saleId: Int): Flow<List<DeliveryNote>>
    fun getDeliveryNoteById(id: Int): Flow<DeliveryNote?>
    suspend fun syncDeliveryNotes(accountId: Int): Result<Unit>
    suspend fun createDeliveryNote(
        saleId: Int,
        date: String,
        vehicleNo: String?,
        lrNo: String?,
        notes: String?,
        items: List<DeliveryNoteItemRequest>,
        accountId: Int
    ): Result<DeliveryNote>
    suspend fun deleteDeliveryNote(id: Int): Result<Unit>
}
