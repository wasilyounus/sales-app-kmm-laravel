package com.sales.app.data.repository

import com.sales.app.data.local.dao.SaleDao
import com.sales.app.data.local.dao.SaleItemDao
import com.sales.app.data.local.entity.SaleEntity
import com.sales.app.data.local.entity.SaleItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.SaleItemRequest
import com.sales.app.data.remote.dto.SaleRequest
import com.sales.app.domain.model.Sale
import com.sales.app.domain.model.SaleItem
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SaleRepository(
    private val apiService: ApiService,
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao
) {
    fun getSalesByAccount(accountId: Int): Flow<List<Sale>> {
        return saleDao.getSalesByAccount(accountId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getSaleById(saleId: Int): Flow<Sale?> {
        val saleFlow = saleDao.getSaleById(saleId)
        val itemsFlow = saleItemDao.getSaleItemsBySaleId(saleId)
        
        return combine(saleFlow, itemsFlow) { saleEntity, itemEntities ->
            saleEntity?.toDomainModel(itemEntities.map { it.toDomainModel() })
        }
    }
    
    suspend fun syncSales(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getSales(accountId)
            
            if (response.success) {
                val entities = response.data.map { dto ->
                    SaleEntity(
                        id = dto.id,
                        partyId = dto.party_id,
                        date = dto.date,
                        invoiceNo = dto.invoice_no,
                        taxId = dto.tax_id,
                        accountId = dto.account_id,
                        createdAt = "",
                        updatedAt = "",
                        deletedAt = dto.deleted_at
                    )
                }
                saleDao.insertSales(entities)
                
                // Also sync items
                syncSaleItems(accountId)
                
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync sales")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    private suspend fun syncSaleItems(accountId: Int) {
        try {
            val response = apiService.getSaleItems(accountId)
            if (response.success) {
                val entities = response.data.map { dto ->
                    SaleItemEntity(
                        id = dto.id,
                        saleId = dto.sale_id,
                        itemId = dto.item_id,
                        price = dto.price,
                        qty = dto.qty,
                        taxId = dto.tax_id,
                        accountId = dto.account_id,
                        createdAt = "",
                        updatedAt = ""
                    )
                }
                saleItemDao.insertSaleItems(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun createSale(
        partyId: Int,
        date: String,
        invoiceNo: String,
        taxId: Int?,
        items: List<SaleItemRequest>,
        accountId: Int
    ): Result<Sale> {
        return try {
            val request = SaleRequest(
                party_id = partyId,
                date = date,
                invoice_no = invoiceNo,
                tax_id = taxId,
                account_id = accountId,
                items = items
            )
            val response = apiService.createSale(request)
            
            if (response.success) {
                val dto = response.data
                val entity = SaleEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    invoiceNo = dto.invoice_no,
                    taxId = dto.tax_id,
                    accountId = dto.account_id,
                    createdAt = "",
                    updatedAt = "",
                    deletedAt = dto.deleted_at
                )
                saleDao.insertSale(entity)
                
                // Save items from response if present
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        SaleItemEntity(
                            id = itemDto.id,
                            saleId = itemDto.sale_id,
                            itemId = itemDto.item_id,
                            price = itemDto.price,
                            qty = itemDto.qty,
                            taxId = itemDto.tax_id,
                            accountId = itemDto.account_id,
                            createdAt = "",
                            updatedAt = ""
                        )
                    }
                    saleItemDao.insertSaleItems(itemEntities)
                }
                
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to create sale")
            }
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }
    
    suspend fun updateSale(
        id: Int,
        partyId: Int,
        date: String,
        invoiceNo: String,
        taxId: Int?,
        items: List<SaleItemRequest>,
        accountId: Int
    ): Result<Sale> {
        return try {
            val request = SaleRequest(
                party_id = partyId,
                date = date,
                invoice_no = invoiceNo,
                tax_id = taxId,
                account_id = accountId,
                items = items
            )
            val response = apiService.updateSale(id, request)
            
            if (response.success) {
                val dto = response.data
                val entity = SaleEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    invoiceNo = dto.invoice_no,
                    taxId = dto.tax_id,
                    accountId = dto.account_id,
                    createdAt = "",
                    updatedAt = "",
                    deletedAt = dto.deleted_at
                )
                saleDao.updateSale(entity)
                
                // Save items from response if present
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        SaleItemEntity(
                            id = itemDto.id,
                            saleId = itemDto.sale_id,
                            itemId = itemDto.item_id,
                            price = itemDto.price,
                            qty = itemDto.qty,
                            taxId = itemDto.tax_id,
                            accountId = itemDto.account_id,
                            createdAt = "",
                            updatedAt = ""
                        )
                    }
                    saleItemDao.insertSaleItems(itemEntities)
                }
                
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to update sale")
            }
        } catch (e: Exception) {
            Result.Error("Update failed: ${e.message}", e)
        }
    }
    
    suspend fun deleteSale(id: Int): Result<Unit> {
        return try {
            apiService.deleteSale(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    private fun SaleEntity.toDomainModel(items: List<SaleItem> = emptyList()) = Sale(
        id = id,
        partyId = partyId,
        date = date,
        invoiceNo = invoiceNo,
        taxId = taxId,
        accountId = accountId,
        items = items
    )
    
    private fun SaleItemEntity.toDomainModel() = SaleItem(
        id = id,
        saleId = saleId,
        itemId = itemId,
        price = price,
        qty = qty,
        taxId = taxId,
        accountId = accountId
    )
}
