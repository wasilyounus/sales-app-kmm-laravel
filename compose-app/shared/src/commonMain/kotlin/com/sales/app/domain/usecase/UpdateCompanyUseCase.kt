package com.sales.app.domain.usecase

import com.sales.app.domain.model.Company
import com.sales.app.domain.repository.AccountRepository
import com.sales.app.util.Result

class UpdateAccountUseCase(
    private val companyRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Result<Account> {
        return companyRepository.updateAccount(account)
    }
}
