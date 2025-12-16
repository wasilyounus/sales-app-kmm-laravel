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
import kotlinx.coroutines.flow.firstOrNull

class CompanyRepositoryImpl(
    private val apiService: ApiService,
    private val companyDao: CompanyDao,
    private val contactDao: com.sales.app.data.local.dao.ContactDao
) : CompanyRepository {
    
    override fun getCompany(): Flow<Company?> {
        return companyDao.getAllCompanies().map { companies ->
            // For now, return the first company as default if no ID is specified
            val companyEntity = companies.firstOrNull()
            if (companyEntity != null) {
                val contacts = contactDao.getContactsForCompany(companyEntity.id)
                companyEntity.toCompany(contacts)
            } else {
                null
            }
        }
    }

    override fun getCompanies(): Flow<List<Company>> {
        return companyDao.getAllCompanies().map { entities ->
            entities.map { entity ->
                // We might want to load contacts for all companies, or maybe just basic info is enough for list
                // Loading contacts for list might be N+1 query issue if not careful, 
                // but with Room it's locally fast.
                // For switcher, names are enough.
                entity.toCompany()
            }
        }
    }

    override fun getCompanyById(id: Int): Flow<Company?> {
        return companyDao.getCompanyById(id).map { entity ->
            if (entity != null) {
                val contacts = contactDao.getContactsForCompany(entity.id)
                entity.toCompany(contacts)
            } else {
                null
            }
        }
    }
    
    override suspend fun updateCompany(company: Company): Result<Company> {
        return try {
            val dto = company.toDto()
            val response = apiService.updateCompany(company.id, dto)
            
            if (response.success && response.data != null) {
                val entity = response.data.toEntity()
                companyDao.insertCompany(entity)
                
                // Sync in isolation: Save contacts separately
                val contactEntities = response.data.contacts.map { it.toEntity(entity.id) }
                contactDao.syncContacts(entity.id, contactEntities)
                
                // Fetch fresh contacts to return complete object
                val contacts = contactDao.getContactsForCompany(entity.id)
                Result.Success(entity.toCompany(contacts))
            } else {
                Result.Error(response.message ?: "Failed to update company")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun fetchCompany(id: Int) {
        try {
            val response = apiService.getCompany(id)
            if (response.success && response.data != null) {
                val entity = response.data.toEntity()
                companyDao.insertCompany(entity)
                
                // Sync in isolation: Save contacts separately
                val contactEntities = response.data.contacts.map { it.toEntity(entity.id) }
                contactDao.syncContacts(entity.id, contactEntities)
            }
        } catch (e: Exception) {
            // Handle error silently or log
            e.printStackTrace()
        }
    }

    override suspend fun fetchCompanies() {
        println("CompanyRepositoryImpl: fetchCompanies called")
        try {
            val response = apiService.getCompanies()
            println("CompanyRepositoryImpl: getCompanies response received, count: ${response.companies.size}")
            response.companies.forEach { dto ->
                // Check if company exists to avoid overwriting detailed data with partial data
                val existing = companyDao.getCompanyById(dto.id).firstOrNull()
                
                if (existing == null) {
                    // Insert new with defaults
                    val newEntity = com.sales.app.data.local.entity.CompanyEntity(
                         id = dto.id,
                         name = dto.name,
                         nameFormatted = dto.nameFormatted,
                         desc = "",
                         taxationType = 0, // Default Int
                         taxRate = 0, // Default Int
                         address = "",
                         call = "",
                         whatsapp = "",
                         footerContent = "",
                         signature = null,
                         financialYearStart = com.sales.app.util.TimeProvider.now().toString(),
                         country = "India",
                         state = "",
                         taxNumber = "",
                         defaultTaxId = null,
                         createdAt = com.sales.app.util.TimeProvider.now().toString(),
                         updatedAt = com.sales.app.util.TimeProvider.now().toString(),
                         deletedAt = null,
                         enableDeliveryNotes = true,
                         enableGrns = true,
                         darkMode = false,
                         taxApplicationLevel = "item"
                    )
                    companyDao.insertCompany(newEntity)
                } else {
                    // Update only name if changed, preserve other details
                    if (existing.name != dto.name || existing.nameFormatted != dto.nameFormatted) {
                        companyDao.updateCompany(existing.copy(
                            name = dto.name, 
                            nameFormatted = dto.nameFormatted
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            // Error handling for fetchCompanies
            println("CompanyRepositoryImpl: fetchCompanies error: ${e.message}")
            e.printStackTrace()
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
        darkMode = darkMode,
        taxApplicationLevel = taxApplicationLevel
    )
}

private fun CompanyEntity.toCompany(contacts: List<com.sales.app.data.local.entity.ContactEntity> = emptyList()): Company {
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
        darkMode = darkMode,
        taxApplicationLevel = taxApplicationLevel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        contacts = contacts.map {
            com.sales.app.domain.model.Contact(
                id = it.id,
                name = it.name,
                phone = it.phone,
                email = it.email,
                designation = it.designation,
                isPrimary = it.isPrimary
            )
        }
    )
}

private fun com.sales.app.data.remote.dto.ContactDto.toEntity(companyId: Int): com.sales.app.data.local.entity.ContactEntity {
    return com.sales.app.data.local.entity.ContactEntity(
        id = id,
        companyId = companyId,
        name = name,
        phone = phone,
        email = email,
        designation = designation,
        isPrimary = isPrimary
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
        darkMode = darkMode,
        taxApplicationLevel = taxApplicationLevel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        contacts = contacts.map {
            com.sales.app.data.remote.dto.ContactDto(
                id = it.id,
                name = it.name,
                phone = it.phone,
                email = it.email,
                designation = it.designation,
                isPrimary = it.isPrimary
            )
        }
    )
}
