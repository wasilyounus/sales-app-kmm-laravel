package com.sales.app.domain.usecase

import com.sales.app.domain.model.Account
import com.sales.app.domain.repository.AccountRepository
import com.sales.app.util.Result

class UpdateAccountUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Result<Account> {
        return accountRepository.updateAccount(account)
    }
}
