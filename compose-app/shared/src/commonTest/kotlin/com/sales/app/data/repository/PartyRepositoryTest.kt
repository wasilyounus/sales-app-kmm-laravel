package com.sales.app.data.repository

import com.sales.app.data.local.dao.PartyDao
import com.sales.app.data.local.entity.PartyEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.PartyDto
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class PartyRepositoryTest {

    private lateinit var partyDao: PartyDao
    private lateinit var apiService: ApiService
    private lateinit var repository: PartyRepository

    @BeforeTest
    fun setup() {
        partyDao = mockk()
        apiService = mockk()
        repository = PartyRepository(apiService, partyDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getAllParties returns parties from DAO`() = runTest {
        // Given
        val partyEntities = listOf(
            PartyEntity(
                id = 1,
                accountId = 1,
                name = "Test Party 1",
                taxNumber = "TAX123",
                address = "Address 1",
                city = "City 1",
                pincode = "12345",
                phone = "1234567890",
                email = "party1@test.com",
                openingBalance = 0.0,
                type = "customer"
            ),
            PartyEntity(
                id = 2,
                accountId = 1,
                name = "Test Party 2",
                taxNumber = "TAX456",
                address = "Address 2",
                city = "City 2",
                pincode = "67890",
                phone = "0987654321",
                email = "party2@test.com",
                openingBalance = 0.0,
                type = "supplier"
            )
        )
        
        every { partyDao.getAllByAccount(1) } returns flowOf(partyEntities)

        // When
        val result = repository.getAllParties(1).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Test Party 1", result[0].name)
        assertEquals("TAX123", result[0].taxNumber)
        assertEquals("Test Party 2", result[1].name)
        assertEquals("TAX456", result[1].taxNumber)
        verify(exactly = 1) { partyDao.getAllByAccount(1) }
    }

    @Test
    fun `party entity maps taxNumber correctly to domain model`() = runTest {
        // Given
        val partyEntity = PartyEntity(
            id = 1,
            accountId = 1,
            name = "Tax Number Test Party",
            taxNumber = "NEWTAX789",
            address = "Test Address",
            city = "Test City",
            pincode = "11111",
            phone = "1111111111",
            email = "tax@test.com",
            openingBalance = 0.0,
            type = "customer"
        )
        
        every { partyDao.getAllByAccount(1) } returns flowOf(listOf(partyEntity))

        // When
        val result = repository.getAllParties(1).first()

        // Then
        val party = result.first()
        assertEquals(1, party.id)
        assertEquals("Tax Number Test Party", party.name)
        assertEquals("NEWTAX789", party.taxNumber)
        assertNotEquals("gst", party.taxNumber) // Ensure we're not using old field
    }

    @Test
    fun `createParty sends correct taxNumber to API`() = runTest {
        // Given
        val partyDto = PartyDto(
            id = 1,
            accountId = 1,
            name = "New Party",
            taxNumber = "APITAX123",
            address = "API Address",
            city = "API City",
            pincode = "99999",
            phone = "9999999999",
            email = "api@test.com",
            openingBalance = 0.0,
            type = "customer"
        )
        
        coEvery { apiService.createParty(any()) } returns partyDto
        coEvery { partyDao.insert(any()) } just Runs

        // When
        val result = repository.createParty(
            accountId = 1,
            name = "New Party",
            taxNumber = "APITAX123",
            address = "API Address",
            city = "API City",
            pincode = "99999",
            phone = "9999999999",
            email = "api@test.com",
            openingBalance = 0.0,
            type = "customer"
        )

        // Then
        assert(result is Result.Success)
        coVerify { 
            apiService.createParty(
                withArg { request ->
                    assertEquals("APITAX123", request.taxNumber)
                }
            )
        }
    }
}
