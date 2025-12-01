package com.sales.app.domain.repository

import com.sales.app.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccount(): Flow<Account?>
    suspend fun updateAccount(account: Account): com.sales.app.util.Result<Account>
    suspend fun fetchAccount(id: Int)
}
