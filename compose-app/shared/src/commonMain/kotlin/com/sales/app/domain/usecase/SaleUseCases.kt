package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.SaleItemRequest
import com.sales.app.domain.repository.SaleRepository
import com.sales.app.domain.model.Sale
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetSalesUseCase(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(companyId: Int): Flow<List<Sale>> {
        return saleRepository.getSalesByAccount(companyId)
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
        companyId: Int
    ): Result<Sale> {
        return saleRepository.createSale(partyId, date, invoiceNo, taxId, items, companyId)
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
        companyId: Int
    ): Result<Sale> {
        return saleRepository.updateSale(id, partyId, date, invoiceNo, taxId, items, companyId)
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
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return saleRepository.syncSales(companyId)
    }
}
