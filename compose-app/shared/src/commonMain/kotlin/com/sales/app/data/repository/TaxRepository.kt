package com.sales.app.data.repository

import com.sales.app.data.local.dao.TaxDao
import com.sales.app.data.local.entity.TaxEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.Tax
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaxRepository(
    private val apiService: ApiService,
    private val taxDao: TaxDao
) {
    fun getAllActiveTaxes(): Flow<List<Tax>> {
        return taxDao.getAllActive().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getAllTaxes(): Flow<List<Tax>> {
        return taxDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    suspend fun syncTaxes(): com.sales.app.util.Result<Unit> {
        return try {
            // Assuming there's an API endpoint for taxes
            // For now, we'll skip actual API call since it might not exist yet
            com.sales.app.util.Result.Success(Unit)
        } catch (e: Exception) {
            com.sales.app.util.Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    private fun TaxEntity.toDomainModel() = Tax(
        id = id,
        name = schemeName,
        tax1Name = tax1Name,
        tax1Rate = tax1Val?.toDouble(),
        tax2Name = tax2Name,
        tax2Rate = tax2Val?.toDouble(),
        tax3Name = tax3Name,
        tax3Rate = tax3Val?.toDouble(),
        tax4Name = tax4Name,
        tax4Rate = tax4Val?.toDouble(),
        active = active
    )
}
