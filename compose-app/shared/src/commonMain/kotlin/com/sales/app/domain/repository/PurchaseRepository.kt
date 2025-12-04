package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.PurchaseItemRequest
import com.sales.app.domain.model.Purchase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    fun getPurchasesByAccount(accountId: Int): Flow<List<Purchase>>
    fun getPurchaseById(purchaseId: Int): Flow<Purchase?>
    suspend fun syncPurchases(accountId: Int): Result<Unit>
    suspend fun createPurchase(
        partyId: Int,
        date: String,
        items: List<PurchaseItemRequest>,
        accountId: Int
    ): Result<Purchase>
    suspend fun updatePurchase(
        id: Int,
        partyId: Int,
        date: String,
        items: List<PurchaseItemRequest>,
        accountId: Int
    ): Result<Purchase>
    suspend fun deletePurchase(id: Int): Result<Unit>
}
