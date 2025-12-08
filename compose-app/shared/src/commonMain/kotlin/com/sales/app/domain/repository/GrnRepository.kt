package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.GrnItemRequest
import com.sales.app.domain.model.Grn
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface GrnRepository {
    fun getGrnsByAccount(accountId: Int): Flow<List<Grn>>
    fun getGrnsByPurchase(purchaseId: Int): Flow<List<Grn>>
    fun getGrnById(id: Int): Flow<Grn?>
    suspend fun syncGrns(accountId: Int): Result<Unit>
    suspend fun createGrn(
        purchaseId: Int,
        date: String,
        vehicleNo: String?,
        invoiceNo: String?,
        notes: String?,
        items: List<GrnItemRequest>,
        accountId: Int
    ): Result<Grn>
    suspend fun deleteGrn(id: Int): Result<Unit>
}
