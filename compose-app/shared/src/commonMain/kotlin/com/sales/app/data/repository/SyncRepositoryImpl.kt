package com.sales.app.data.repository

import com.sales.app.data.local.dao.ItemDao
import com.sales.app.data.local.dao.PartyDao
import com.sales.app.data.local.dao.SyncDao
import com.sales.app.data.local.dao.TaxDao
import com.sales.app.data.local.dao.UqcDao
import com.sales.app.data.local.entity.ItemEntity
import com.sales.app.data.local.entity.PartyEntity
import com.sales.app.data.local.entity.SyncTimestampEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

import com.sales.app.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val apiService: ApiService,
    private val itemDao: ItemDao,
    private val partyDao: PartyDao,
    private val taxDao: TaxDao,
    private val uqcDao: UqcDao,
    private val syncDao: SyncDao
) : SyncRepository {
    override suspend fun syncMasterData(accountId: Int): Result<Unit> {
        return try {
            val lastSync = syncDao.getSyncTimestamp("master_data")
            val timestamp = lastSync?.timestamp ?: "1970-01-01 00:00:00"
            
            val response = apiService.syncMasterData(accountId, timestamp)
            
            if (response.success) {
                // Save items
                response.data.items?.let { items ->
                    val entities = items.map { dto ->
                        ItemEntity(
                            id = dto.id,
                            name = dto.name,
                            altName = dto.alt_name,
                            brand = dto.brand,
                            size = dto.size,
                            uqc = dto.uqc,
                            hsn = dto.hsn,
                            accountId = dto.account_id,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at,
                            deletedAt = dto.deleted_at
                        )
                    }
                    itemDao.insertItems(entities)
                }
                
                // Save parties
                response.data.parties?.let { parties ->
                    val entities = parties.map { dto ->
                        PartyEntity(
                            id = dto.id,
                            name = dto.name,
                            taxNumber = dto.taxNumber,
                            phone = dto.phone,
                            email = dto.email,
                            accountId = dto.account_id,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at,
                            deletedAt = dto.deleted_at
                        )
                    }
                    partyDao.insertParties(entities)
                }
                
                // Save taxes
                response.data.taxes?.let { taxes ->
                    val entities = taxes.map { dto ->
                        com.sales.app.data.local.entity.TaxEntity(
                            id = dto.id,
                            schemeName = dto.scheme_name,
                            country = dto.country,
                            tax1Name = dto.tax1_name,
                            tax1Val = dto.tax1_val,
                            tax2Name = dto.tax2_name,
                            tax2Val = dto.tax2_val,
                            tax3Name = dto.tax3_name,
                            tax3Val = dto.tax3_val,
                            tax4Name = dto.tax4_name,
                            tax4Val = dto.tax4_val,
                            active = dto.active,
                            createdAt = "",
                            updatedAt = ""
                        )
                    }
                    taxDao.insertAll(entities)
                }
                
                // Save uqcs
                response.data.uqcs?.let { uqcs ->
                    val entities = uqcs.map { dto ->
                        com.sales.app.data.local.entity.UqcEntity(
                            id = dto.id,
                            code = dto.uqc,
                            name = dto.quantity ?: "",
                            active = dto.active,
                            createdAt = "",
                            updatedAt = ""
                        )
                    }
                    uqcDao.insertAll(entities)
                }
                
                // Update sync timestamp
                syncDao.insertSyncTimestamp(
                    SyncTimestampEntity(
                        key = "master_data",
                        timestamp = response.data.timestamp,
                        lastSyncedAt = Clock.System.now().toEpochMilliseconds()
                    )
                )
                
                Result.Success(Unit)
            } else {
                Result.Error("Sync failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    override suspend fun fullSync(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.fullSync(accountId)
            
            if (response.success) {
                // Clear existing data
                itemDao.deleteItemsByAccount(accountId)
                partyDao.deletePartiesByAccount(accountId)
                
                // Save all data
                response.data.items?.let { items ->
                    val entities = items.map { dto ->
                        ItemEntity(
                            id = dto.id,
                            name = dto.name,
                            altName = dto.alt_name,
                            brand = dto.brand,
                            size = dto.size,
                            uqc = dto.uqc,
                            hsn = dto.hsn,
                            accountId = dto.account_id,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at,
                            deletedAt = dto.deleted_at
                        )
                    }
                    itemDao.insertItems(entities)
                }
                
                response.data.parties?.let { parties ->
                    val entities = parties.map { dto ->
                        PartyEntity(
                            id = dto.id,
                            name = dto.name,
                            taxNumber = dto.taxNumber,
                            phone = dto.phone,
                            email = dto.email,
                            accountId = dto.account_id,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at,
                            deletedAt = dto.deleted_at
                        )
                    }
                    partyDao.insertParties(entities)
                }
                
                // Update sync timestamp
                syncDao.insertSyncTimestamp(
                    SyncTimestampEntity(
                        key = "full_sync",
                        timestamp = response.data.timestamp,
                        lastSyncedAt = Clock.System.now().toEpochMilliseconds()
                    )
                )
                
                Result.Success(Unit)
            } else {
                Result.Error("Full sync failed")
            }
        } catch (e: Exception) {
            Result.Error("Full sync failed: ${e.message}", e)
        }
    }
    
    override fun getSyncStatus(): Flow<SyncTimestampEntity?> = flow {
        emit(syncDao.getSyncTimestamp("master_data"))
    }
}
