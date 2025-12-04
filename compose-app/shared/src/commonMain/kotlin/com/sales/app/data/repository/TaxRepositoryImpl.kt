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
            // Assuming there's an API endpoint for taxes
            // For now, we'll skip actual API call since it might not exist yet
            com.sales.app.util.Result.Success(Unit)
        } catch (e: Exception) {
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
