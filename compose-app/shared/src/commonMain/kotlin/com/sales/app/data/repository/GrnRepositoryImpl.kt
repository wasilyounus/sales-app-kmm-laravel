package com.sales.app.data.repository

import com.sales.app.data.local.dao.GrnDao
import com.sales.app.data.local.entity.GrnEntity
import com.sales.app.data.local.entity.GrnItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.GrnItemRequest
import com.sales.app.data.remote.dto.GrnRequest
import com.sales.app.domain.model.Grn
import com.sales.app.domain.model.GrnItem
import com.sales.app.domain.repository.GrnRepository
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GrnRepositoryImpl(
    private val apiService: ApiService,
    private val grnDao: GrnDao
) : GrnRepository {
    
    override fun getGrnsByAccount(companyId: Int): Flow<List<Grn>> {
        return grnDao.getGrnsByAccount(companyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getGrnsByPurchase(purchaseId: Int): Flow<List<Grn>> {
        return grnDao.getGrnsByPurchase(purchaseId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getGrnById(id: Int): Flow<Grn?> {
        val grnFlow = grnDao.getGrnById(id)
        val itemsFlow = grnDao.getGrnItems(id)
        
        return combine(grnFlow, itemsFlow) { entity, itemEntities ->
            entity?.toDomainModel(itemEntities.map { it.toDomainModel() })
        }
    }
    
    override suspend fun syncGrns(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getGrns(accountId)
            
            val entities = response.data.map { dto ->
                GrnEntity(
                    id = dto.id,
                    purchaseId = dto.purchase_id,
                    grnNo = dto.grn_no,
                    date = dto.date,
                    vehicleNo = dto.vehicle_no,
                    invoiceNo = dto.invoice_no,
                    notes = dto.notes,
                    companyId = dto.company_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            grnDao.insertGrns(entities)
            
            // Sync items
            response.data.forEach { dto ->
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        GrnItemEntity(
                            id = itemDto.id,
                            grnId = itemDto.grn_id,
                            itemId = itemDto.item_id,
                            quantity = itemDto.quantity,
                            createdAt = itemDto.created_at ?: "",
                            updatedAt = itemDto.updated_at ?: ""
                        )
                    }
                    grnDao.insertGrnItems(itemEntities)
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    override suspend fun createGrn(
        purchaseId: Int,
        date: String,
        vehicleNo: String?,
        invoiceNo: String?,
        notes: String?,
        items: List<GrnItemRequest>,
        accountId: Int
    ): Result<Grn> {
        return try {
            val request = GrnRequest(
                purchase_id = purchaseId,
                date = date,
                vehicle_no = vehicleNo,
                invoice_no = invoiceNo,
                notes = notes,
                items = items
            )
            val response = apiService.createGrn(accountId, request)
            
            val dto = response.data
            val entity = GrnEntity(
                id = dto.id,
                purchaseId = dto.purchase_id,
                grnNo = dto.grn_no,
                date = dto.date,
                vehicleNo = dto.vehicle_no,
                invoiceNo = dto.invoice_no,
                notes = dto.notes,
                companyId = dto.company_id,
                createdAt = dto.created_at ?: "",
                updatedAt = dto.updated_at ?: "",
                deletedAt = dto.deleted_at
            )
            grnDao.insertGrn(entity)
            
            // Save items
            dto.items?.let { items ->
                val itemEntities = items.map { itemDto ->
                    GrnItemEntity(
                        id = itemDto.id,
                        grnId = itemDto.grn_id,
                        itemId = itemDto.item_id,
                        quantity = itemDto.quantity,
                        createdAt = itemDto.created_at ?: "",
                        updatedAt = itemDto.updated_at ?: ""
                    )
                }
                grnDao.insertGrnItems(itemEntities)
            }
            
            Result.Success(entity.toDomainModel())
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }

    override suspend fun updateGrn(
        id: Int,
        purchaseId: Int,
        date: String,
        vehicleNo: String?,
        invoiceNo: String?,
        notes: String?,
        items: List<GrnItemRequest>,
        accountId: Int
    ): Result<Grn> {
        return try {
            val request = GrnRequest(
                purchase_id = purchaseId,
                date = date,
                vehicle_no = vehicleNo,
                invoice_no = invoiceNo,
                notes = notes,
                items = items
            )
            val response = apiService.updateGrn(id, request)

            val dto = response.data
            // Since response might not include deletedAt, keep it null or handle accordingly
            val entity = GrnEntity(
                id = dto.id,
                purchaseId = dto.purchase_id,
                grnNo = dto.grn_no,
                date = dto.date,
                vehicleNo = dto.vehicle_no,
                invoiceNo = dto.invoice_no,
                notes = dto.notes,
                companyId = dto.company_id,
                createdAt = dto.created_at ?: "",
                updatedAt = dto.updated_at ?: "",
                deletedAt = dto.deleted_at
            )
            grnDao.insertGrn(entity)

            // Update items: Delete old and insert new. 
            // Simplified approach: Clear old items for this GRN locally and re-insert new ones.
            // This prevents ghost items.
            grnDao.deleteGrnItems(dto.id)

            dto.items?.let { items ->
                val itemEntities = items.map { itemDto ->
                    GrnItemEntity(
                        id = itemDto.id,
                        grnId = itemDto.grn_id,
                        itemId = itemDto.item_id,
                        quantity = itemDto.quantity,
                        createdAt = itemDto.created_at ?: "",
                        updatedAt = itemDto.updated_at ?: ""
                    )
                }
                grnDao.insertGrnItems(itemEntities)
            }

            Result.Success(entity.toDomainModel())
        } catch (e: Exception) {
            Result.Error("Update failed: ${e.message}", e)
        }
    }
    
    override suspend fun deleteGrn(id: Int): Result<Unit> {
        return try {
            apiService.deleteGrn(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    private fun GrnEntity.toDomainModel(items: List<GrnItem> = emptyList()) = Grn(
        id = id,
        purchaseId = purchaseId,
        grnNo = grnNo,
        date = date,
        vehicleNo = vehicleNo,
        invoiceNo = invoiceNo,
        notes = notes,
        companyId = companyId,
        items = items
    )
    
    private fun GrnItemEntity.toDomainModel() = GrnItem(
        id = id,
        grnId = grnId,
        itemId = itemId,
        quantity = quantity
    )
}
