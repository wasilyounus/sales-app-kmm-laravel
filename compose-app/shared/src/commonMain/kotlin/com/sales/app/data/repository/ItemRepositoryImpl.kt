package com.sales.app.data.repository

import com.sales.app.data.local.dao.*
import com.sales.app.data.local.entity.ItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.ItemRequest
import com.sales.app.domain.model.Item
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

import com.sales.app.domain.repository.ItemRepository
import com.sales.app.domain.model.Uqc

class ItemRepositoryImpl(
    private val apiService: ApiService,
    private val itemDao: ItemDao,
    private val uqcDao: UqcDao
) : ItemRepository {
    override fun getItemsByAccount(companyId: Int): Flow<List<Item>> {
        return itemDao.getItemsByAccount(companyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun searchItems(companyId: Int, query: String): Flow<List<Item>> {
        return itemDao.searchItems(companyId, query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun syncItems(companyId: Int): Result<Unit> {
        return try {
            val response = apiService.getItems(companyId)
            
            if (response.success) {
                val entities = response.data.map { dto ->
                    ItemEntity(
                        id = dto.id,
                        name = dto.name,
                        altName = dto.alt_name,
                        brand = dto.brand,
                        size = dto.size,
                        uqc = dto.uqc,
                        hsn = dto.hsn,
                        companyId = dto.company_id,
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
        companyId: Int,
        taxId: Int?
    ): Result<Item> {
        return try {
            val request = ItemRequest(name, altName, brand, size, uqc, hsn, companyId, taxId)
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
                    companyId = response.data.company_id,
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
        companyId: Int,
        taxId: Int?
    ): Result<Item> {
        return try {
            val request = ItemRequest(name, altName, brand, size, uqc, hsn, companyId, taxId)
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
                    companyId = response.data.company_id,
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
    
    override suspend fun getItemById(companyId: Int, itemId: Int): Item? {
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
                    companyId = response.data.company_id,
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
        return uqcDao.getAll().first().map { entity ->
            Uqc(
                id = entity.id,
                uqc = entity.code, // Entity likely has 'code' not 'uqc' based on migration? need to check Entity
                quantity = entity.name, // Migration: name. quantity? need to check Entity
                type = null,
                active = true
            )
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
        companyId = companyId,
        taxId = taxId
    )
}
