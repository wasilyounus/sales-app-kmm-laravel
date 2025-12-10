package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.PurchaseItemRequest
import com.sales.app.domain.repository.PurchaseRepository
import com.sales.app.domain.model.Purchase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetPurchasesUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(companyId: Int): Flow<List<Purchase>> {
        return purchaseRepository.getPurchasesByAccount(companyId)
    }
}

class GetPurchaseByIdUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(purchaseId: Int): Flow<Purchase?> {
        return purchaseRepository.getPurchaseById(purchaseId)
    }
}

class CreatePurchaseUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(
        partyId: Int,
        date: String,
        items: List<PurchaseItemRequest>,
        companyId: Int,
        invoiceNo: String? = null
    ): Result<Purchase> {
        return purchaseRepository.createPurchase(partyId, date, items, companyId, invoiceNo)
    }
}

class UpdatePurchaseUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(
        id: Int,
        partyId: Int,
        date: String,
        items: List<PurchaseItemRequest>,
        companyId: Int,
        invoiceNo: String? = null
    ): Result<Purchase> {
        return purchaseRepository.updatePurchase(id, partyId, date, items, companyId, invoiceNo)
    }
}

class DeletePurchaseUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return purchaseRepository.deletePurchase(id)
    }
}

class SyncPurchasesUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return purchaseRepository.syncPurchases(companyId)
    }
}
