package com.sales.app.data.repository

import com.sales.app.data.local.dao.SaleDao
import com.sales.app.data.local.dao.SaleItemDao
import com.sales.app.data.local.entity.SaleEntity
import com.sales.app.data.local.entity.SaleItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.*
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class SaleRepositoryImplTest {

    private lateinit var saleDao: SaleDao
    private lateinit var saleItemDao: SaleItemDao
    private lateinit var apiService: ApiService
    private lateinit var repository: SaleRepositoryImpl

    @BeforeTest
    fun setup() {
        saleDao = mockk(relaxed = true)
        saleItemDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = SaleRepositoryImpl(apiService, saleDao, saleItemDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getSaleById returns mapped domain model with items`() = runTest {
        // Given
        val saleEntity = SaleEntity(
            id = 1,
            partyId = 10,
            date = "2024-01-01",
            invoiceNo = "INV-001",
            accountId = 1,
            taxId = 5,
            createdAt = "",
            updatedAt = "",
            deletedAt = null
        )
        val itemEntities = listOf(
            SaleItemEntity(
                id = 100,
                saleId = 1,
                itemId = 50,
                price = 100.0,
                qty = 2.0,
                taxId = 5,
                accountId = 1,
                logId = 0,
                createdAt = "",
                updatedAt = "",
                deletedAt = null
            )
        )
        every { saleDao.getSaleById(1) } returns flowOf(saleEntity)
        every { saleItemDao.getSaleItemsBySaleId(1) } returns flowOf(itemEntities)

        // When
        val result = repository.getSaleById(1).first()

        // Then
        assertNotNull(result)
        assertEquals("INV-001", result.invoiceNo)
        assertEquals(1, result.items.size)
        assertEquals(100.0, result.items[0].price)
    }

    @Test
    fun `createSale calls API and inserts Sale and Items into DAO`() = runTest {
        // Given
        val saleDto = SaleDto(
            id = 1,
            party_id = 10,
            date = "2024-01-01",
            invoice_no = "INV-001",
            tax_id = 5,
            account_id = 1,
            log_id = 0,
            created_at = "2024-01-01",
            updated_at = "2024-01-01",
            deleted_at = null,
            items = listOf(
                SaleItemDto(
                    id = 100,
                    sale_id = 1,
                    item_id = 50,
                    price = 100.0,
                    qty = 2.0,
                    tax_id = 5,
                    account_id = 1,
                    log_id = 0
                )
            )
        )
        val response = SaleResponse(success = true, data = saleDto)
        
        coEvery { apiService.createSale(any()) } returns response
        coEvery { saleDao.insertSale(any()) } returns 1L
        coEvery { saleItemDao.insertSaleItems(any()) } just Runs

        // When
        val itemsRequest = listOf(
            SaleItemRequest(item_id = 50, price = 100.0, qty = 2.0)
        )
        val result = repository.createSale(
            partyId = 10,
            date = "2024-01-01",
            invoiceNo = "INV-001",
            taxId = 5,
            items = itemsRequest,
            accountId = 1
        )

        // Then
        assert(result is Result.Success)
        coVerify { 
            saleDao.insertSale(
                withArg { entity ->
                    assertEquals(1, entity.id)
                    assertEquals("INV-001", entity.invoiceNo)
                }
            )
        }
        coVerify {
            saleItemDao.insertSaleItems(
                withArg { entities ->
                    assertEquals(1, entities.size)
                    assertEquals(100, entities[0].id)
                }
            )
        }
    }
}
