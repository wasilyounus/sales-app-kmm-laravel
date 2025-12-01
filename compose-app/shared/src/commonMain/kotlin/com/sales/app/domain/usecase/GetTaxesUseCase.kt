package com.sales.app.domain.usecase

import com.sales.app.data.repository.TaxRepository
import com.sales.app.domain.model.Tax
import kotlinx.coroutines.flow.Flow

class GetTaxesUseCase(
    private val taxRepository: TaxRepository
) {
    operator fun invoke(): Flow<List<Tax>> {
        return taxRepository.getAllActiveTaxes()
    }
}
