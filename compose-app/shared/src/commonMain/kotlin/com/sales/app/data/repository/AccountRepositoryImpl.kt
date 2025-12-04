package com.sales.app.data.repository

import com.sales.app.data.local.dao.AccountDao
import com.sales.app.data.local.entity.AccountEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.Account
import com.sales.app.domain.repository.AccountRepository
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val apiService: ApiService,
    private val accountDao: AccountDao
) : AccountRepository {
    
    override fun getAccount(): Flow<Account?> {
        return accountDao.getAllAccounts().map { accounts ->
            accounts.firstOrNull()?.toAccount()
        }
    }
    
    override fun getAccountById(id: Int): Flow<Account?> {
        return accountDao.getAccountById(id).map { it?.toAccount() }
    }
    
    override suspend fun fetchAccount(id: Int) {
        try {
            val response = apiService.getAccount(id)
            if (response.success && response.data != null) {
                val entity = response.data.toEntity()
                accountDao.insertAccount(entity)
            }
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    override suspend fun updateAccount(account: Account): Result<Account> {
        return try {
            val dto = account.toDto()
            val response = apiService.updateAccount(account.id, dto)
            
            if (response.success && response.data != null) {
                val entity = response.data.toEntity()
                accountDao.insertAccount(entity)
                Result.Success(entity.toAccount())
            } else {
                Result.Error(response.message ?: "Failed to update account")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}

private fun com.sales.app.data.remote.dto.AccountDto.toEntity(): AccountEntity {
    return AccountEntity(
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
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}

private fun AccountEntity.toAccount(): Account {
    return Account(
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
        financialYearStart = financialYearStart
    )
}

private fun Account.toDto(): com.sales.app.data.remote.dto.AccountDto {
    return com.sales.app.data.remote.dto.AccountDto(
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
        signature = signature?.toBoolean(),
        financialYearStart = financialYearStart
    )
}
