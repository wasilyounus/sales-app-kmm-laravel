package com.sales.app.domain.usecase

import com.sales.app.domain.repository.CompanyRepository

class FetchCompaniesUseCase(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke() {
        repository.fetchCompanies()
    }
}
