package com.sales.app.data.repository

import com.sales.app.data.local.dao.DeliveryNoteDao
import com.sales.app.data.local.entity.DeliveryNoteEntity
import com.sales.app.data.local.entity.DeliveryNoteItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.DeliveryNoteItemRequest
import com.sales.app.data.remote.dto.DeliveryNoteRequest
import com.sales.app.domain.model.DeliveryNote
import com.sales.app.domain.model.DeliveryNoteItem
import com.sales.app.domain.repository.DeliveryNoteRepository
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DeliveryNoteRepositoryImpl(
    private val apiService: ApiService,
    private val deliveryNoteDao: DeliveryNoteDao
) : DeliveryNoteRepository {
    
    override fun getDeliveryNotesByAccount(accountId: Int): Flow<List<DeliveryNote>> {
        return deliveryNoteDao.getDeliveryNotesByAccount(accountId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getDeliveryNotesBySale(saleId: Int): Flow<List<DeliveryNote>> {
        return deliveryNoteDao.getDeliveryNotesBySale(saleId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getDeliveryNoteById(id: Int): Flow<DeliveryNote?> {
        val dnFlow = deliveryNoteDao.getDeliveryNoteById(id)
        val itemsFlow = deliveryNoteDao.getDeliveryNoteItems(id)
        
        return combine(dnFlow, itemsFlow) { entity, itemEntities ->
            entity?.toDomainModel(itemEntities.map { it.toDomainModel() })
        }
    }
    
    override suspend fun syncDeliveryNotes(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getDeliveryNotes(accountId)
            
            val entities = response.data.map { dto ->
                DeliveryNoteEntity(
                    id = dto.id,
                    saleId = dto.sale_id,
                    dnNumber = dto.dn_number,
                    date = dto.date,
                    vehicleNo = dto.vehicle_no,
                    lrNo = dto.lr_no,
                    notes = dto.notes,
                    accountId = dto.account_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            deliveryNoteDao.insertDeliveryNotes(entities)
            
            // Sync items
            response.data.forEach { dto ->
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        DeliveryNoteItemEntity(
                            id = itemDto.id,
                            deliveryNoteId = itemDto.delivery_note_id,
                            itemId = itemDto.item_id,
                            quantity = itemDto.quantity,
                            createdAt = itemDto.created_at ?: "",
                            updatedAt = itemDto.updated_at ?: ""
                        )
                    }
                    deliveryNoteDao.insertDeliveryNoteItems(itemEntities)
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    override suspend fun createDeliveryNote(
        saleId: Int,
        date: String,
        vehicleNo: String?,
        lrNo: String?,
        notes: String?,
        items: List<DeliveryNoteItemRequest>,
        accountId: Int
    ): Result<DeliveryNote> {
        return try {
            val request = DeliveryNoteRequest(
                sale_id = saleId,
                date = date,
                vehicle_no = vehicleNo,
                lr_no = lrNo,
                notes = notes,
                items = items
            )
            val response = apiService.createDeliveryNote(accountId, request)
            
            val dto = response.data
            val entity = DeliveryNoteEntity(
                id = dto.id,
                saleId = dto.sale_id,
                dnNumber = dto.dn_number,
                date = dto.date,
                vehicleNo = dto.vehicle_no,
                lrNo = dto.lr_no,
                notes = dto.notes,
                accountId = dto.account_id,
                createdAt = dto.created_at ?: "",
                updatedAt = dto.updated_at ?: "",
                deletedAt = dto.deleted_at
            )
            deliveryNoteDao.insertDeliveryNote(entity)
            
            // Save items
            dto.items?.let { items ->
                val itemEntities = items.map { itemDto ->
                    DeliveryNoteItemEntity(
                        id = itemDto.id,
                        deliveryNoteId = itemDto.delivery_note_id,
                        itemId = itemDto.item_id,
                        quantity = itemDto.quantity,
                        createdAt = itemDto.created_at ?: "",
                        updatedAt = itemDto.updated_at ?: ""
                    )
                }
                deliveryNoteDao.insertDeliveryNoteItems(itemEntities)
            }
            
            Result.Success(entity.toDomainModel())
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }
    
    override suspend fun deleteDeliveryNote(id: Int): Result<Unit> {
        return try {
            apiService.deleteDeliveryNote(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    private fun DeliveryNoteEntity.toDomainModel(items: List<DeliveryNoteItem> = emptyList()) = DeliveryNote(
        id = id,
        saleId = saleId,
        dnNumber = dnNumber,
        date = date,
        vehicleNo = vehicleNo,
        lrNo = lrNo,
        notes = notes,
        accountId = accountId,
        items = items
    )
    
    private fun DeliveryNoteItemEntity.toDomainModel() = DeliveryNoteItem(
        id = id,
        deliveryNoteId = deliveryNoteId,
        itemId = itemId,
        quantity = quantity
    )
}
