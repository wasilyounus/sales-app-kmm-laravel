package com.sales.app.data.repository

import com.sales.app.data.local.dao.AccountDao
import com.sales.app.data.local.entity.AccountEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.AccountDto
import com.sales.app.data.remote.dto.AccountResponse
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AccountRepositoryTest {

    private lateinit var accountDao: AccountDao
    private lateinit var apiService: ApiService
    private lateinit var repository: AccountRepositoryImpl

    @BeforeTest
    fun setup() {
        accountDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = AccountRepositoryImpl(apiService, accountDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getAccount returns mapped domain model from DAO`() = runTest {
        // Given
        val entity = AccountEntity(
            id = 1,
            name = "Test Account",
            nameFormatted = "Test Account Pvt Ltd",
            desc = "Description",
            taxationType = 1,
            taxRate = 18,
            address = "Address Line 1",
            call = "1234567890",
            whatsapp = "0987654321",
            footerContent = "Footer",
            signature = "true",
            financialYearStart = "04-01",
            country = "India",
            state = "Kerala",
            taxNumber = "GSTIN123",
            defaultTaxId = 10,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            deletedAt = null
        )
        every { accountDao.getAllAccounts() } returns flowOf(listOf(entity))

        // When
        val result = repository.getAccount().first()

        // Then
        assertNotNull(result)
        assertEquals(1, result.id)
        assertEquals("Test Account", result.name)
        assertEquals("India", result.country)
        assertEquals("Kerala", result.state)
        assertEquals("GSTIN123", result.taxNumber)
        assertEquals(10, result.defaultTaxId)
    }

    @Test
    fun `fetchAccount calls API and inserts into DAO on success`() = runTest {
        // Given
        val dto = AccountDto(
            id = 1,
            name = "API Account",
            nameFormatted = "API Account Ltd",
            desc = "Desc",
            taxationType = 1,
            taxRate = 18,
            address = "API Address",
            call = "111",
            whatsapp = "222",
            footerContent = "Footer",
            signature = true,
            financialYearStart = "04-01",
            country = "US",
            state = "NY",
            taxNumber = "TAX999",
            defaultTaxId = 5,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        val response = AccountResponse(success = true, data = dto, message = "Success")
        
        coEvery { apiService.getAccount(1) } returns response
        coEvery { accountDao.insertAccount(any()) } just Runs

        // When
        repository.fetchAccount(1)

        // Then
        coVerify { apiService.getAccount(1) }
        coVerify { 
            accountDao.insertAccount(
                withArg { entity ->
                    assertEquals(1, entity.id)
                    assertEquals("API Account", entity.name)
                    assertEquals("US", entity.country)
                    assertEquals("TAX999", entity.taxNumber)
                }
            )
        }
    }
}
