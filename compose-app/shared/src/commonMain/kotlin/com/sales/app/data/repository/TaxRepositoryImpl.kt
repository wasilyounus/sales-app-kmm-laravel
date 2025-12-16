package com.sales.app.data.repository

import com.sales.app.data.local.dao.TaxDao
import com.sales.app.data.local.entity.TaxEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.Tax
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.sales.app.domain.repository.TaxRepository

class TaxRepositoryImpl(
    private val apiService: ApiService,
    private val taxDao: TaxDao
) : TaxRepository {
    override fun getAllActiveTaxes(): Flow<List<Tax>> {
        return taxDao.getAllActive().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getAllTaxes(): Flow<List<Tax>> {
        return taxDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getActiveTaxesByCountry(country: String?): Flow<List<Tax>> {
        return taxDao.getAllActive().map { entities ->
            entities
                .filter { entity ->
                    // Show taxes where country matches or is null (global taxes like "No Tax")
                    entity.country == null || entity.country == country
                }
                .map { it.toDomainModel() }
        }
    }
    
    override suspend fun syncTaxes(): com.sales.app.util.Result<Unit> {
        return try {
            val response = apiService.getTaxes()
            if (response.success) {
                val entities = response.data.map { dto ->
                    TaxEntity(
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
                        createdAt = dto.created_at ?: "",
                        updatedAt = dto.updated_at ?: ""
                    )
                }
                taxDao.deleteAll() // Clear old taxes to remove deleted ones
                taxDao.insertAll(entities)
                com.sales.app.util.Result.Success(Unit)
            } else {
                com.sales.app.util.Result.Error("Failed to sync taxes")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            com.sales.app.util.Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    private fun TaxEntity.toDomainModel() = Tax(
        id = id,
        schemeName = schemeName,
        country = country,
        tax1Name = tax1Name,
        tax1Val = tax1Val,
        tax2Name = tax2Name,
        tax2Val = tax2Val,
        tax3Name = tax3Name,
        tax3Val = tax3Val,
        tax4Name = tax4Name,
        tax4Val = tax4Val,
        active = active
    )

}
