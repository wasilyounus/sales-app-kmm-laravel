package com.sales.app.domain.usecase

import com.sales.app.domain.repository.AccountRepository

class FetchAccountUseCase(
    private val companyRepository: AccountRepository
) {
    suspend operator fun invoke(companyId: Int) {
        companyRepository.fetchAccount(companyId)
    }
}
