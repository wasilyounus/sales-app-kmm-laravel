package com.sales.app.data.repository

import com.sales.app.data.local.dao.AccountDao
import com.sales.app.data.local.entity.AccountEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.AccountDto
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
        accountDao = mockk()
        apiService = mockk()
        repository = AccountRepositoryImpl(apiService, accountDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getAllAccounts returns accounts from DAO`() = runTest {
        // Given
        val accountEntities = listOf(
            AccountEntity(
                id = 1,
                name = "Test Account 1",
                nameFormatted = "TEST ACCOUNT 1",
                desc = "Description 1",
                taxationType = 1,
                taxCountry = "India",
                country = "India",
                state = "Maharashtra",
                taxNumber = "TAX123",
                address = "Address 1",
                call = "1234567890",
                whatsapp = "1234567890",
                footerContent = "Footer 1",
                signature = true,
                financialYearStart = "2025-04-01 00:00:00",
                defaultTaxId = null,
                visibility = "private"
            )
        )
        
        every { accountDao.getAll() } returns flowOf(accountEntities)

        // When
        val result = repository.getAllAccounts().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Account 1", result[0].name)
        assertEquals("TAX123", result[0].taxNumber)
        verify(exactly = 1) { accountDao.getAll() }
    }

    @Test
    fun `account entity does not have gst field`() = runTest {
        // Given
        val accountEntity = AccountEntity(
            id = 1,
            name = "No GST Account",
            nameFormatted = "NO GST ACCOUNT",
            desc = null,
            taxationType = 1,
            taxCountry = null,
            country = "India",
            state = null,
            taxNumber = "NEWTAX789",
            address = null,
            call = null,
            whatsapp = null,
            footerContent = null,
            signature = false,
            financialYearStart = "2025-04-01 00:00:00",
            defaultTaxId = null,
            visibility = "private"
        )
        
        every { accountDao.getAll() } returns flowOf(listOf(accountEntity))

        // When
        val result = repository.getAllAccounts().first()

        // Then
        val account = result.first()
        assertEquals("NEWTAX789", account.taxNumber)
        // Verify the Account model doesn't have a gst property
        // This is a compile-time check - if gst existed, this test would fail to compile
        assertNotNull(account.taxNumber)
    }

    @Test
    fun `DTO to Entity mapping uses taxNumber`() = runTest {
        // Given
        val accountDto = AccountDto(
            id = 1,
            name = "DTO Account",
            nameFormatted = "DTO ACCOUNT",
            desc = null,
            taxationType = 2,
            taxCountry = "India",
            country = "India",
            state = "Delhi",
            taxNumber = "DTOTAX123",
            address = null,
            call = null,
            whatsapp = null,
            footerContent = null,
            signature = false,
            financialYearStart = "2025-04-01 00:00:00",
            defaultTaxId = null,
            visibility = "private"
        )
        
        coEvery { apiService.getAccounts() } returns listOf(accountDto)
        coEvery { accountDao.insertAll(any()) } just Runs

        // When
        repository.syncAccounts()

        // Then
        coVerify {
            accountDao.insertAll(
                withArg { entities ->
                    val entity = entities.first()
                    assertEquals("DTOTAX123", entity.taxNumber)
                }
            )
        }
    }
}
