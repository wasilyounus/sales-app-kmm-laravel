package com.sales.app.domain.usecase

import com.sales.app.domain.repository.CompanyRepository

class FetchCompanyUseCase(
    private val companyRepository: CompanyRepository
) {
    suspend operator fun invoke(companyId: Int) {
        companyRepository.fetchCompany(companyId)
    }
}
