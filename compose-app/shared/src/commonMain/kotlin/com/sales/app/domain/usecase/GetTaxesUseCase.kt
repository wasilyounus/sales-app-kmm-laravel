package com.sales.app.domain.usecase

import com.sales.app.domain.repository.TaxRepository
import com.sales.app.domain.model.Tax
import kotlinx.coroutines.flow.Flow

class GetTaxesUseCase(
    private val taxRepository: TaxRepository
) {
    operator fun invoke(country: String? = null): Flow<List<Tax>> {
        return if (country != null) {
            taxRepository.getActiveTaxesByCountry(country)
        } else {
            taxRepository.getAllActiveTaxes()
        }
    }
}
