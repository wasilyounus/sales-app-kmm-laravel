package com.sales.app.data.repository

import com.sales.app.data.local.dao.AddressDao
import com.sales.app.data.local.dao.PartyDao
import com.sales.app.data.local.entity.PartyEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.PartyDto
import com.sales.app.data.remote.dto.PartyResponse
import com.sales.app.data.remote.dto.AddressRequest
import com.sales.app.data.remote.dto.AddressDto
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class PartyRepositoryTest {

    private lateinit var partyDao: PartyDao
    private lateinit var addressDao: AddressDao
    private lateinit var apiService: ApiService
    private lateinit var repository: PartyRepositoryImpl

    @BeforeTest
    fun setup() {
        partyDao = mockk(relaxed = true)
        addressDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = PartyRepositoryImpl(apiService, partyDao, addressDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getPartiesByAccount returns parties from DAO`() = runTest {
        // Given
        val partyEntities = listOf(
            PartyEntity(
                id = 1,
                accountId = 1,
                name = "Test Party 1",
                taxNumber = "TAX123",
                phone = "1234567890",
                email = "party1@test.com",
                createdAt = "2024-01-01",
                updatedAt = "2024-01-01",
                deletedAt = null
            ),
            PartyEntity(
                id = 2,
                accountId = 1,
                name = "Test Party 2",
                taxNumber = "TAX456",
                phone = "0987654321",
                email = "party2@test.com",
                createdAt = "2024-01-01",
                updatedAt = "2024-01-01",
                deletedAt = null
            )
        )
        
        every { partyDao.getPartiesByAccount(1) } returns flowOf(partyEntities)

        // When
        val result = repository.getPartiesByAccount(1).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Test Party 1", result[0].name)
        assertEquals("TAX123", result[0].taxNumber)
        assertEquals("Test Party 2", result[1].name)
        assertEquals("TAX456", result[1].taxNumber)
        verify(exactly = 1) { partyDao.getPartiesByAccount(1) }
    }

    @Test
    fun `party entity maps taxNumber correctly to domain model`() = runTest {
        // Given
        val partyEntity = PartyEntity(
            id = 1,
            accountId = 1,
            name = "Tax Number Test Party",
            taxNumber = "NEWTAX789",
            phone = "1111111111",
            email = "tax@test.com",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            deletedAt = null
        )
        
        every { partyDao.getPartiesByAccount(1) } returns flowOf(listOf(partyEntity))

        // When
        val result = repository.getPartiesByAccount(1).first()

        // Then
        val party = result.first()
        assertEquals(1, party.id)
        assertEquals("Tax Number Test Party", party.name)
        assertEquals("NEWTAX789", party.taxNumber)
    }

    @Test
    fun `createParty sends correct taxNumber to API`() = runTest {
        // Given
        val partyDto = PartyDto(
            id = 1,
            account_id = 1,
 // Dto might have both or one, checking definition
            name = "New Party",
            taxNumber = "APITAX123",
            phone = "9999999999",
            email = "api@test.com",
            created_at = "2024-01-01",
            updated_at = "2024-01-01",
            deleted_at = null,
            addresses = listOf(
                AddressDto(
                    id = 1,
                    party_id = 1,
                    account_id = 1,
                    line1 = "Line 1",
                    line2 = "Line 2",
                    place = "API City",
                    district = "District",
                    state = "State",
                    pincode = "99999",
                    country = "Country",
                    latitude = 0.0,
                    longitude = 0.0,
                    created_at = "2024-01-01",
                    updated_at = "2024-01-01"
                )
            )
        )
        
        val partyResponse = PartyResponse(
            success = true,
            data = partyDto
        )
        
        coEvery { apiService.createParty(any()) } returns partyResponse
        coEvery { partyDao.insertParty(any()) } just Runs
        coEvery { addressDao.insertAddresses(any()) } just Runs

        // When
        val addresses = listOf(
            AddressRequest(
                line1 = "Line 1",
                line2 = "Line 2",
                place = "API City",
                state = "State",
                pincode = "99999",
                country = "Country"
            )
        )

        val result = repository.createParty(
            accountId = 1,
            name = "New Party",
            taxNumber = "APITAX123",
            phone = "9999999999",
            email = "api@test.com",
            addresses = addresses
        )

        // Then
        assert(result is Result.Success)
        coVerify { 
            apiService.createParty(
                withArg { request ->
                    assertEquals("APITAX123", request.taxNumber)
                    assertEquals("API City", request.addresses?.first()?.place)
                }
            )
        }
    }
}
