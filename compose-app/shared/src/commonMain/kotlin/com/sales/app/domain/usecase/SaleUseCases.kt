package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.SaleItemRequest
import com.sales.app.data.repository.SaleRepository
import com.sales.app.domain.model.Sale
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetSalesUseCase(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(accountId: Int): Flow<List<Sale>> {
        return saleRepository.getSalesByAccount(accountId)
    }
}

class GetSaleByIdUseCase(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(saleId: Int): Flow<Sale?> {
        return saleRepository.getSaleById(saleId)
    }
}

class CreateSaleUseCase(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(
        partyId: Int,
        date: String,
        invoiceNo: String,
        taxId: Int?,
        items: List<SaleItemRequest>,
        accountId: Int
    ): Result<Sale> {
        return saleRepository.createSale(partyId, date, invoiceNo, taxId, items, accountId)
    }
}

class UpdateSaleUseCase(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(
        id: Int,
        partyId: Int,
        date: String,
        invoiceNo: String,
        taxId: Int?,
        items: List<SaleItemRequest>,
        accountId: Int
    ): Result<Sale> {
        return saleRepository.updateSale(id, partyId, date, invoiceNo, taxId, items, accountId)
    }
}

class DeleteSaleUseCase(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return saleRepository.deleteSale(id)
    }
}

class SyncSalesUseCase(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Unit> {
        return saleRepository.syncSales(accountId)
    }
}
