package com.sales.app.domain.usecase

import com.sales.app.domain.model.Company
import com.sales.app.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetCompaniesUseCase(
    private val repository: CompanyRepository
) {
    operator fun invoke(): Flow<List<Company>> {
        return repository.getCompanies()
    }
}
