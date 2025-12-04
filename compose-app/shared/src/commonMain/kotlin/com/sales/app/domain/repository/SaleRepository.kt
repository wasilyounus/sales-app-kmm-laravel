package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.SaleItemRequest
import com.sales.app.domain.model.Sale
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface SaleRepository {
    fun getSalesByAccount(accountId: Int): Flow<List<Sale>>
    fun getSaleById(saleId: Int): Flow<Sale?>
    suspend fun syncSales(accountId: Int): Result<Unit>
    suspend fun createSale(
        partyId: Int,
        date: String,
        invoiceNo: String,
        taxId: Int?,
        items: List<SaleItemRequest>,
        accountId: Int
    ): Result<Sale>
    suspend fun updateSale(
        id: Int,
        partyId: Int,
        date: String,
        invoiceNo: String,
        taxId: Int?,
        items: List<SaleItemRequest>,
        accountId: Int
    ): Result<Sale>
    suspend fun deleteSale(id: Int): Result<Unit>
}
