package com.sales.app.domain.usecase

import com.sales.app.domain.model.Company
import com.sales.app.domain.repository.CompanyRepository
import com.sales.app.util.Result

class UpdateCompanyUseCase(
    private val companyRepository: CompanyRepository
) {
    suspend operator fun invoke(company: Company): Result<Company> {
        return companyRepository.updateCompany(company)
    }
}
