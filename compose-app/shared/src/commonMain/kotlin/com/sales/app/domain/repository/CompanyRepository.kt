package com.sales.app.domain.repository

import com.sales.app.domain.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun getCompany(): Flow<Company?>
    fun getCompanies(): Flow<List<Company>>
    fun getCompanyById(id: Int): Flow<Company?>
    suspend fun updateCompany(company: Company): com.sales.app.util.Result<Company>
    suspend fun fetchCompany(id: Int)
    suspend fun fetchCompanies()
}
