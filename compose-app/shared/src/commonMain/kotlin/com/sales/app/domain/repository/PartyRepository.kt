package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.AddressRequest
import com.sales.app.domain.model.Party
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface PartyRepository {
    fun getPartiesByAccount(companyId: Int): Flow<List<Party>>
    fun searchParties(companyId: Int, query: String): Flow<List<Party>>
    suspend fun syncParties(accountId: Int): Result<Unit>
    suspend fun createParty(
        name: String,
        taxNumber: String?,
        phone: String?,
        email: String?,
        addresses: List<AddressRequest>,
        accountId: Int
    ): Result<Party>
    suspend fun updateParty(
        id: Int,
        name: String,
        taxNumber: String?,
        phone: String?,
        email: String?,
        addresses: List<AddressRequest>,
        accountId: Int
    ): Result<Party>
    suspend fun deleteParty(id: Int): Result<Unit>
    fun getPartyById(accountId: Int, partyId: Int): Flow<Party?>
}
