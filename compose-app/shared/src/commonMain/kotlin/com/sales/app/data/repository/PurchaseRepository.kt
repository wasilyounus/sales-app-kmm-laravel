package com.sales.app.data.repository

import com.sales.app.data.local.dao.PurchaseDao
import com.sales.app.data.local.dao.PurchaseItemDao
import com.sales.app.data.local.entity.PurchaseEntity
import com.sales.app.data.local.entity.PurchaseItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.PurchaseItemRequest
import com.sales.app.data.remote.dto.PurchaseRequest
import com.sales.app.domain.model.Purchase
import com.sales.app.domain.model.PurchaseItem
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PurchaseRepository(
    private val apiService: ApiService,
    private val purchaseDao: PurchaseDao,
    private val purchaseItemDao: PurchaseItemDao
) {
    fun getPurchasesByAccount(accountId: Int): Flow<List<Purchase>> {
        return purchaseDao.getPurchasesByAccount(accountId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getPurchaseById(purchaseId: Int): Flow<Purchase?> {
        val purchaseFlow = purchaseDao.getPurchaseById(purchaseId)
        val itemsFlow = purchaseItemDao.getPurchaseItemsByPurchaseId(purchaseId)
        
        return combine(purchaseFlow, itemsFlow) { purchaseEntity, itemEntities ->
            purchaseEntity?.toDomainModel(itemEntities.map { it.toDomainModel() })
        }
    }
    
    suspend fun syncPurchases(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getPurchases(accountId)
            
            if (response.success) {
                val entities = response.data.map { dto ->
                    PurchaseEntity(
                        id = dto.id,
                        partyId = dto.party_id,
                        date = dto.date,
                        accountId = dto.account_id,
                        createdAt = "",
                        updatedAt = "",
                        deletedAt = dto.deleted_at
                    )
                }
                purchaseDao.insertPurchases(entities)
                
                // Also sync items
                syncPurchaseItems(accountId)
                
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync purchases")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    private suspend fun syncPurchaseItems(accountId: Int) {
        try {
            val response = apiService.getPurchaseItems(accountId)
            if (response.success) {
                val entities = response.data.map { dto ->
                    PurchaseItemEntity(
                        id = dto.id,
                        purchaseId = dto.purchase_id,
                        itemId = dto.item_id,
                        price = dto.price,
                        qty = dto.qty,
                        taxId = dto.tax_id,
                        accountId = dto.account_id,
                        logId = 0,
                        createdAt = "",
                        updatedAt = "",
                        deletedAt = null
                    )
                }
                purchaseItemDao.insertPurchaseItems(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun createPurchase(
        partyId: Int,
        date: String,
        items: List<PurchaseItemRequest>,
        accountId: Int
    ): Result<Purchase> {
        return try {
            val request = PurchaseRequest(
                party_id = partyId,
                date = date,
                account_id = accountId,
                items = items
            )
            val response = apiService.createPurchase(request)
            
            if (response.success) {
                val dto = response.data
                val entity = PurchaseEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    accountId = dto.account_id,
                    createdAt = "",
                    updatedAt = "",
                    deletedAt = dto.deleted_at
                )
                purchaseDao.insertPurchase(entity)
                
                // Save items from response if present
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        PurchaseItemEntity(
                            id = itemDto.id,
                            purchaseId = itemDto.purchase_id,
                            itemId = itemDto.item_id,
                            price = itemDto.price,
                            qty = itemDto.qty,
                            taxId = itemDto.tax_id,
                            accountId = itemDto.account_id,
                            logId = 0,
                            createdAt = "",
                            updatedAt = "",
                            deletedAt = null
                        )
                    }
                    purchaseItemDao.insertPurchaseItems(itemEntities)
                }
                
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to create purchase")
            }
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }
    
    suspend fun updatePurchase(
        id: Int,
        partyId: Int,
        date: String,
        items: List<PurchaseItemRequest>,
        accountId: Int
    ): Result<Purchase> {
        return try {
            val request = PurchaseRequest(
                party_id = partyId,
                date = date,
                account_id = accountId,
                items = items
            )
            val response = apiService.updatePurchase(id, request)
            
            if (response.success) {
                val dto = response.data
                val entity = PurchaseEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    accountId = dto.account_id,
                    createdAt = "",
                    updatedAt = "",
                    deletedAt = dto.deleted_at
                )
                purchaseDao.updatePurchase(entity)
                
                // Save items from response if present
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        PurchaseItemEntity(
                            id = itemDto.id,
                            purchaseId = itemDto.purchase_id,
                            itemId = itemDto.item_id,
                            price = itemDto.price,
                            qty = itemDto.qty,
                            taxId = itemDto.tax_id,
                            accountId = itemDto.account_id,
                            logId = 0,
                            createdAt = "",
                            updatedAt = "",
                            deletedAt = null
                        )
                    }
                    purchaseItemDao.insertPurchaseItems(itemEntities)
                }
                
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to update purchase")
            }
        } catch (e: Exception) {
            Result.Error("Update failed: ${e.message}", e)
        }
    }
    
    suspend fun deletePurchase(id: Int): Result<Unit> {
        return try {
            apiService.deletePurchase(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    private fun PurchaseEntity.toDomainModel(items: List<PurchaseItem> = emptyList()) = Purchase(
        id = id,
        partyId = partyId,
        date = date,
        accountId = accountId,
        items = items
    )
    
    private fun PurchaseItemEntity.toDomainModel() = PurchaseItem(
        id = id,
        purchaseId = purchaseId,
        itemId = itemId,
        price = price,
        qty = qty,
        taxId = taxId,
        accountId = accountId
    )
}
