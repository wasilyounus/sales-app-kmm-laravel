@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.data.repository

import com.sales.app.data.local.dao.CompanyDao
import com.sales.app.data.local.entity.CompanyEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.Company
import com.sales.app.domain.repository.CompanyRepository
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CompanyRepositoryImpl(
    private val apiService: ApiService,
    private val companyDao: CompanyDao
) : CompanyRepository {
    
    override fun getCompany(): Flow<Company?> {
        return companyDao.getAllCompanies().map { companies ->
            companies.firstOrNull()?.toCompany()
        }
    }
    
    override fun getCompanyById(id: Int): Flow<Company?> {
        return companyDao.getCompanyById(id).map { it?.toCompany() }
    }
    
    override suspend fun fetchCompany(id: Int) {
        try {
            val response = apiService.getAccount(id)
            if (response.success && response.data != null) {
                val entity = response.data.toEntity()
                companyDao.insertCompany(entity)
            }
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    override suspend fun updateCompany(company: Company): Result<Company> {
        return try {
            val dto = company.toDto()
            val response = apiService.updateAccount(company.id, dto)
            
            if (response.success && response.data != null) {
                val entity = response.data.toEntity()
                companyDao.insertCompany(entity)
                Result.Success(entity.toCompany())
            } else {
                Result.Error(response.message ?: "Failed to update company")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}

private fun com.sales.app.data.remote.dto.CompanyDto.toEntity(): CompanyEntity {
    return CompanyEntity(
        id = id,
        name = name,
        nameFormatted = nameFormatted,
        desc = desc,
        taxationType = taxationType,
        taxRate = taxRate,

        address = address,
        call = call,
        whatsapp = whatsapp,
        footerContent = footerContent,
        signature = signature?.toString(),
        financialYearStart = financialYearStart,
        country = country,
        state = state,
        taxNumber = taxNumber,
        defaultTaxId = defaultTaxId,
        createdAt = createdAt ?: com.sales.app.util.TimeProvider.now().toString(),
        updatedAt = updatedAt ?: com.sales.app.util.TimeProvider.now().toString(),
        deletedAt = deletedAt,
        enableDeliveryNotes = enableDeliveryNotes,
        enableGrns = enableGrns,
        taxApplicationLevel = taxApplicationLevel
    )
}

private fun CompanyEntity.toCompany(): Company {
    return Company(
        id = id,
        name = name,
        nameFormatted = nameFormatted,
        desc = desc,
        taxationType = taxationType,
        taxRate = taxRate,

        address = address,
        call = call,
        whatsapp = whatsapp,
        footerContent = footerContent,
        signature = signature,
        financialYearStart = financialYearStart,
        country = country,
        state = state,
        taxNumber = taxNumber,
        defaultTaxId = defaultTaxId,
        enableDeliveryNotes = enableDeliveryNotes,
        enableGrns = enableGrns,
        taxApplicationLevel = taxApplicationLevel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}

private fun Company.toDto(): com.sales.app.data.remote.dto.CompanyDto {
    return com.sales.app.data.remote.dto.CompanyDto(
        id = id,
        name = name,
        nameFormatted = nameFormatted,
        desc = desc,
        taxationType = taxationType,
        taxRate = taxRate,

        address = address,
        call = call,
        whatsapp = whatsapp,
        footerContent = footerContent,
        signature = signature?.toBooleanStrictOrNull() ?: false,
        financialYearStart = financialYearStart,
        country = country,
        state = state,
        taxNumber = taxNumber,
        defaultTaxId = defaultTaxId,
        enableDeliveryNotes = enableDeliveryNotes,
        enableGrns = enableGrns,
        taxApplicationLevel = taxApplicationLevel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
