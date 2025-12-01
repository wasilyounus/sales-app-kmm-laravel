package com.sales.app.domain.usecase

import com.sales.app.domain.repository.AccountRepository

class FetchAccountUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(accountId: Int) {
        accountRepository.fetchAccount(accountId)
    }
}
