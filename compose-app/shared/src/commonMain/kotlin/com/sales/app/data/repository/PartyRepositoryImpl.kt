package com.sales.app.data.repository

import com.sales.app.data.local.dao.AddressDao
import com.sales.app.data.local.dao.PartyDao
import com.sales.app.data.local.entity.AddressEntity
import com.sales.app.data.local.entity.PartyEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.AddressDto
import com.sales.app.data.remote.dto.AddressRequest
import com.sales.app.data.remote.dto.PartyRequest
import com.sales.app.domain.model.Address
import com.sales.app.domain.model.Party
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

import com.sales.app.domain.repository.PartyRepository

class PartyRepositoryImpl(
    private val apiService: ApiService,
    private val partyDao: PartyDao,
    private val addressDao: AddressDao
) : PartyRepository {
    override fun getPartiesByAccount(companyId: Int): Flow<List<Party>> {
        return partyDao.getPartiesByAccount(companyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun searchParties(companyId: Int, query: String): Flow<List<Party>> {
        return partyDao.searchParties(companyId, query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun syncParties(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getParties(accountId)
            if (response.success) {
                val parties = response.data
                val partyEntities = parties.map { dto ->
                    PartyEntity(
                        id = dto.id,
                        name = dto.name,
                        taxNumber = dto.taxNumber,
                        phone = dto.phone,
                        email = dto.email,
                        companyId = dto.company_id,
                        createdAt = dto.created_at,
                        updatedAt = dto.updated_at,
                        deletedAt = dto.deleted_at
                    )
                }
                partyDao.insertParties(partyEntities)
                
                // Sync Addresses
                val addressEntities = parties.flatMap { party ->
                    party.addresses?.map { addr ->
                        AddressEntity(
                            id = addr.id,
                            partyId = party.id,
                            companyId = party.company_id ?: 0,
                            line1 = addr.line1 ?: "",
                            line2 = addr.line2,
                            place = addr.place ?: "",
                            district = addr.district,
                            state = addr.state ?: "",
                            pincode = addr.pincode ?: "",
                            country = addr.country ?: "",
                            latitude = addr.latitude,
                            longitude = addr.longitude,
                            createdAt = addr.created_at ?: "",
                            updatedAt = addr.updated_at ?: ""
                        )
                    } ?: emptyList()
                }
                addressDao.insertAddresses(addressEntities)
                
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync parties")
            }
        } catch (e: Exception) {
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    override suspend fun createParty(
        name: String,
        taxNumber: String?,
        phone: String?,
        email: String?,
        addresses: List<AddressRequest>,
        accountId: Int
    ): Result<Party> {
        return try {
            val request = PartyRequest(
                name = name, 
                taxNumber = taxNumber, 
                phone = phone, 
                email = email, 
                company_id = accountId,
                addresses = addresses
            )
            val response = apiService.createParty(request)
            
            if (response.success) {
                val dto = response.data
                val entity = PartyEntity(
                    id = dto.id,
                    name = dto.name,
                    taxNumber = dto.taxNumber,
                    phone = dto.phone,
                    email = dto.email,
                    companyId = dto.company_id,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at,
                    deletedAt = dto.deleted_at
                )
                partyDao.insertParty(entity)
                
                // Save addresses
                dto.addresses?.let { dtos ->
                    val addressEntities = dtos.map { addr ->
                        AddressEntity(
                            id = addr.id,
                            partyId = dto.id,
                            companyId = dto.company_id ?: 0,
                            line1 = addr.line1 ?: "",
                            line2 = addr.line2,
                            place = addr.place ?: "",
                            district = addr.district,
                            state = addr.state ?: "",
                            pincode = addr.pincode ?: "",
                            country = addr.country ?: "",
                            latitude = addr.latitude,
                            longitude = addr.longitude,
                            createdAt = addr.created_at ?: "",
                            updatedAt = addr.updated_at ?: ""
                        )
                    }
                    addressDao.insertAddresses(addressEntities)
                }
                
                Result.Success(entity.toDomainModel(
                    dto.addresses?.map { it.toDomainModel() } ?: emptyList()
                ))
            } else {
                Result.Error("Failed to create party")
            }
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }
    
    override suspend fun updateParty(
        id: Int,
        name: String,
        taxNumber: String?,
        phone: String?,
        email: String?,
        addresses: List<AddressRequest>,
        accountId: Int
    ): Result<Party> {
        return try {
            val request = PartyRequest(
                name = name, 
                taxNumber = taxNumber, 
                phone = phone, 
                email = email, 
                company_id = accountId,
                addresses = addresses
            )
            val response = apiService.updateParty(id, request)
            
            if (response.success) {
                val dto = response.data
                val entity = PartyEntity(
                    id = dto.id,
                    name = dto.name,
                    taxNumber = dto.taxNumber,
                    phone = dto.phone,
                    email = dto.email,
                    companyId = dto.company_id,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at,
                    deletedAt = dto.deleted_at
                )
                partyDao.updateParty(entity)
                
                // Update addresses (replace)
                addressDao.deleteAddressesByPartyId(id)
                dto.addresses?.let { dtos ->
                    val addressEntities = dtos.map { addr ->
                        AddressEntity(
                            id = addr.id,
                            partyId = dto.id,
                            companyId = dto.company_id ?: 0,
                            line1 = addr.line1 ?: "",
                            line2 = addr.line2,
                            place = addr.place ?: "",
                            district = addr.district,
                            state = addr.state ?: "",
                            pincode = addr.pincode ?: "",
                            country = addr.country ?: "",
                            latitude = addr.latitude,
                            longitude = addr.longitude,
                            createdAt = addr.created_at ?: "",
                            updatedAt = addr.updated_at ?: ""
                        )
                    }
                    addressDao.insertAddresses(addressEntities)
                }
                
                Result.Success(entity.toDomainModel(
                    dto.addresses?.map { it.toDomainModel() } ?: emptyList()
                ))
            } else {
                Result.Error("Failed to update party")
            }
        } catch (e: Exception) {
            Result.Error("Update failed: ${e.message}", e)
        }
    }
    
    override suspend fun deleteParty(id: Int): Result<Unit> {
        return try {
            apiService.deleteParty(id)
            // Local delete logic if needed
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    override fun getPartyById(accountId: Int, partyId: Int): Flow<Party?> {
        val partyFlow = partyDao.getPartyById(partyId) // This needs to return Flow
        val addressesFlow = addressDao.getAddressesByPartyId(partyId)
        
        return combine(partyFlow, addressesFlow) { party, addresses ->
            party?.toDomainModel(addresses.map { it.toDomainModel() })
        }
    }
    
    // Helper mappings
    private fun PartyEntity.toDomainModel(addresses: List<Address> = emptyList()) = Party(
        id = id,
        name = name,
        taxNumber = taxNumber,
        phone = phone,
        email = email,
        companyId = companyId,
        addresses = addresses
    )
    
    private fun AddressEntity.toDomainModel() = Address(
        id = id,
        partyId = partyId,
        companyId = companyId,
        line1 = line1,
        line2 = line2,
        place = place,
        district = district,
        state = state,
        pincode = pincode,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
    
    private fun AddressDto.toDomainModel() = Address(
        id = id,
        partyId = party_id,
        companyId = company_id ?: 0,
        line1 = line1 ?: "",
        line2 = line2,
        place = place ?: "",
        district = district,
        state = state ?: "",
        pincode = pincode ?: "",
        country = country ?: "",
        latitude = latitude,
        longitude = longitude
    )
}
