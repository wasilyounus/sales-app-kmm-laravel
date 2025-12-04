package com.sales.app.domain.usecase

import com.sales.app.domain.model.Account
import com.sales.app.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccountUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<Account?> {
        return accountRepository.getAccount()
    }
    
    operator fun invoke(accountId: Int): Flow<Account?> {
        return accountRepository.getAccountById(accountId)
    }
}
