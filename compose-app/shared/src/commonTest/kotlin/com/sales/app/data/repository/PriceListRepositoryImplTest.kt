package com.sales.app.data.repository

import com.sales.app.data.local.dao.PriceListDao
import com.sales.app.data.local.entity.PriceListEntity
import com.sales.app.data.local.entity.PriceListItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.PriceListDto
import com.sales.app.domain.model.PriceList
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class PriceListRepositoryImplTest {

    private lateinit var priceListDao: PriceListDao
    private lateinit var apiService: ApiService
    private lateinit var repository: PriceListRepositoryImpl

    @BeforeTest
    fun setup() {
        priceListDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = PriceListRepositoryImpl(apiService, priceListDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getPriceList returns local data if available`() = runTest {
        // Given
        val entity = PriceListEntity(id = 1, name = "Local List", accountId = 1, itemsCount = 2)
        val items = listOf(
            PriceListItemEntity(id = 10, priceListId = 1, itemId = 100, price = 50.0, itemName = "Item A", itemCode = "A", standardPrice = 60.0),
            PriceListItemEntity(id = 11, priceListId = 1, itemId = 101, price = 20.0, itemName = "Item B", itemCode = "B", standardPrice = 25.0)
        )
        
        coEvery { priceListDao.getPriceList(1) } returns entity
        coEvery { priceListDao.getPriceListItems(1) } returns items

        // When
        val result = repository.getPriceList(1)

        // Then
        assert(result.isSuccess)
        val priceList = result.getOrThrow()
        assertEquals("Local List", priceList.name)
        assertEquals(2, priceList.items.size)
        assertEquals(50.0, priceList.items[0].price)
        
        coVerify(exactly = 0) { apiService.getPriceList(any()) }
    }

    @Test
    fun `getPriceList calls API if local data is missing`() = runTest {
        // Given
        val dto = PriceListDto(id = 1, name = "API List", itemsCount = 0, items = emptyList())
        
        coEvery { priceListDao.getPriceList(1) } returns null
        coEvery { apiService.getPriceList(1) } returns dto

        // When
        val result = repository.getPriceList(1)

        // Then
        assert(result.isSuccess)
        assertEquals("API List", result.getOrThrow().name)
        coVerify { apiService.getPriceList(1) }
    }
}
