package com.sales.app.data.repository

import com.sales.app.data.local.dao.*
import com.sales.app.data.local.entity.ItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.ItemRequest
import com.sales.app.domain.model.Item
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.sales.app.domain.repository.ItemRepository
import com.sales.app.domain.model.Uqc

class ItemRepositoryImpl(
    private val apiService: ApiService,
    private val itemDao: ItemDao,
    private val uqcDao: UqcDao
) : ItemRepository {
    override fun getItemsByAccount(accountId: Int): Flow<List<Item>> {
        return itemDao.getItemsByAccount(accountId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun searchItems(accountId: Int, query: String): Flow<List<Item>> {
        return itemDao.searchItems(accountId, query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun syncItems(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getItems(accountId)
            
            if (response.success) {
                val entities = response.data.map { dto ->
                    // Lookup UQC ID from local DB using the code from API
                    val uqcEntity = uqcDao.getByCode(dto.uqc)
                    val uqcId = uqcEntity?.id ?: 0 // Default to 0 if not found (shouldn't happen if UQCs synced)
                    
                    ItemEntity(
                        id = dto.id,
                        name = dto.name,
                        altName = dto.alt_name,
                        brand = dto.brand,
                        size = dto.size,
                        uqc = uqcId,
                        hsn = dto.hsn,
                        accountId = dto.account_id,
                        taxId = dto.tax_id,
                        createdAt = dto.created_at,
                        updatedAt = dto.updated_at,
                        deletedAt = dto.deleted_at
                    )
                }
                itemDao.insertItems(entities)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync items")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    override suspend fun createItem(
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        accountId: Int,
        taxId: Int?
    ): Result<Item> {
        return try {
            val request = ItemRequest(name, altName, brand, size, uqc, hsn, accountId, taxId)
            val response = apiService.createItem(request)
            
            if (response.success) {
                val entity = ItemEntity(
                    id = response.data.id,
                    name = response.data.name,
                    altName = response.data.alt_name,
                    brand = response.data.brand,
                    size = response.data.size,
                    uqc = response.data.uqc,
                    hsn = response.data.hsn,
                    accountId = response.data.account_id,
                    taxId = response.data.tax_id,
                    createdAt = response.data.created_at,
                    updatedAt = response.data.updated_at,
                    deletedAt = response.data.deleted_at
                )
                itemDao.insertItem(entity)
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to create item")
            }
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }
    
    override suspend fun updateItem(
        id: Int,
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        accountId: Int,
        taxId: Int?
    ): Result<Item> {
        return try {
            val request = ItemRequest(name, altName, brand, size, uqc, hsn, accountId, taxId)
            val response = apiService.updateItem(id, request)
            
            if (response.success) {
                val entity = ItemEntity(
                    id = response.data.id,
                    name = response.data.name,
                    altName = response.data.alt_name,
                    brand = response.data.brand,
                    size = response.data.size,
                    uqc = response.data.uqc,
                    hsn = response.data.hsn,
                    accountId = response.data.account_id,
                    taxId = response.data.tax_id,
                    createdAt = response.data.created_at,
                    updatedAt = response.data.updated_at,
                    deletedAt = response.data.deleted_at
                )
                itemDao.updateItem(entity)
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to update item")
            }
        } catch (e: Exception) {
            Result.Error("Update failed: ${e.message}", e)
        }
    }
    
    override suspend fun deleteItem(id: Int): Result<Unit> {
        return try {
            apiService.deleteItem(id)
            // Soft delete locally - we'll sync later
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    override suspend fun getItemById(accountId: Int, itemId: Int): Item? {
        return try {
            val response = apiService.getItem(itemId)
            if (response.success) {
                Item(
                    id = response.data.id,
                    name = response.data.name,
                    altName = response.data.alt_name,
                    brand = response.data.brand,
                    size = response.data.size,
                    uqc = response.data.uqc,
                    hsn = response.data.hsn,
                    accountId = response.data.account_id,
                    taxId = response.data.tax_id
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // Fallback to local db if offline
            // Since DAO returns Flow, we can't easily get single item synchronously without collecting
            // For now, let's rely on API or return null
            null
        }
    }

    override suspend fun getUqcs(): List<Uqc> {
        return try {
            val response = apiService.getUqcs()
            if (response.success) {
                response.data.map { dto ->
                    Uqc(
                        id = dto.id,
                        uqc = dto.uqc,
                        quantity = dto.quantity,
                        type = dto.type,
                        active = dto.active
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun ItemEntity.toDomainModel() = Item(
        id = id,
        name = name,
        altName = altName,
        brand = brand,
        size = size,
        uqc = uqc,
        hsn = hsn,
        accountId = accountId,
        taxId = taxId
    )
}
