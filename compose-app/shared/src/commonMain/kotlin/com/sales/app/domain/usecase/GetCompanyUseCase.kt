package com.sales.app.domain.usecase

import com.sales.app.domain.model.Company
import com.sales.app.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetCompanyUseCase(
    private val companyRepository: CompanyRepository
) {
    operator fun invoke(): Flow<Company?> {
        return companyRepository.getCompany()
    }
    
    operator fun invoke(companyId: Int): Flow<Company?> {
        return companyRepository.getCompanyById(companyId)
    }
}
