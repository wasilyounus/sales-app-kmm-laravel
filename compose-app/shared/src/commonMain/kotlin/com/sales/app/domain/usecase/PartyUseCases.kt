package com.sales.app.domain.usecase

import com.sales.app.data.repository.PartyRepository
import com.sales.app.domain.model.Party
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import com.sales.app.data.remote.dto.AddressRequest

class GetPartiesUseCase(
    private val partyRepository: PartyRepository
) {
    operator fun invoke(accountId: Int): Flow<List<Party>> {
        return partyRepository.getPartiesByAccount(accountId)
    }
}

class SearchPartiesUseCase(
    private val partyRepository: PartyRepository
) {
    operator fun invoke(accountId: Int, query: String): Flow<List<Party>> {
        return if (query.isBlank()) {
            partyRepository.getPartiesByAccount(accountId)
        } else {
            partyRepository.searchParties(accountId, query)
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
        accountId: Int
    ): Result<Party> {
        return partyRepository.createParty(name, taxNumber, phone, email, addresses, accountId)
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
        accountId: Int
    ): Result<Party> {
        return partyRepository.updateParty(id, name, taxNumber, phone, email, addresses, accountId)
    }
}

class GetPartyByIdUseCase(
    private val partyRepository: PartyRepository
) {
    operator fun invoke(accountId: Int, partyId: Int): Flow<Party?> {
        return partyRepository.getPartyById(accountId, partyId)
    }
}
