package com.sales.app.domain.repository

import com.sales.app.domain.model.Tax
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface TaxRepository {
    fun getAllActiveTaxes(): Flow<List<Tax>>
    fun getAllTaxes(): Flow<List<Tax>>
    fun getActiveTaxesByCountry(country: String?): Flow<List<Tax>>
    suspend fun syncTaxes(): Result<Unit>
}
