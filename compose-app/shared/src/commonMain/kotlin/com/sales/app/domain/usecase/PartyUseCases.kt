package com.sales.app.domain.usecase

import com.sales.app.domain.repository.PartyRepository
import com.sales.app.domain.model.Party
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import com.sales.app.data.remote.dto.AddressRequest

class GetPartiesUseCase(
    private val partyRepository: PartyRepository
) {
    operator fun invoke(companyId: Int): Flow<List<Party>> {
        return partyRepository.getPartiesByAccount(companyId)
    }
}

class SearchPartiesUseCase(
    private val partyRepository: PartyRepository
) {
    operator fun invoke(companyId: Int, query: String): Flow<List<Party>> {
        return if (query.isBlank()) {
            partyRepository.getPartiesByAccount(companyId)
        } else {
            partyRepository.searchParties(companyId, query)
        }
    }
}



class CreatePartyUseCase(
    private val partyRepository: PartyRepository
) {
    suspend operator fun invoke(
        name: String,
        taxNumber: String?,
        phone: String?,
        email: String?,
        addresses: List<AddressRequest>,
        companyId: Int
    ): Result<Party> {
        return partyRepository.createParty(name, taxNumber, phone, email, addresses, companyId)
    }
}

class UpdatePartyUseCase(
    private val partyRepository: PartyRepository
) {
    suspend operator fun invoke(
        id: Int,
        name: String,
        taxNumber: String?,
        phone: String?,
        email: String?,
        addresses: List<AddressRequest>,
        companyId: Int
    ): Result<Party> {
        return partyRepository.updateParty(id, name, taxNumber, phone, email, addresses, companyId)
    }
}

class GetPartyByIdUseCase(
    private val partyRepository: PartyRepository
) {
    operator fun invoke(companyId: Int, partyId: Int): Flow<Party?> {
        return partyRepository.getPartyById(companyId, partyId)
    }
}
