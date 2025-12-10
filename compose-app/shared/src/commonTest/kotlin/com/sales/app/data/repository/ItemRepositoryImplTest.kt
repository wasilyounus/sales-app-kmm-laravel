package com.sales.app.data.repository

import com.sales.app.data.local.dao.ItemDao
import com.sales.app.data.local.entity.ItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.ItemDto
import com.sales.app.data.remote.dto.ItemResponse
import com.sales.app.data.remote.dto.ItemsResponse
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import com.sales.app.data.local.dao.UqcDao
import kotlin.test.*

class ItemRepositoryImplTest {

    private lateinit var itemDao: ItemDao
    private lateinit var apiService: ApiService
    private lateinit var uqcDao: UqcDao
    private lateinit var repository: ItemRepositoryImpl

    @BeforeTest
    fun setup() {
        itemDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        uqcDao = mockk(relaxed = true)
        repository = ItemRepositoryImpl(apiService, itemDao, uqcDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getItemsByAccount returns mapped domain models from DAO`() = runTest {
        // Given
        val entity = ItemEntity(
            id = 1,
            name = "Test Item",
            altName = "Alt Item",
            brand = "Brand",
            size = "Large",
            uqc = 1,
            hsn = 1234,
            accountId = 1,
            taxId = 5,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            deletedAt = null
        )
        every { itemDao.getItemsByAccount(1) } returns flowOf(listOf(entity))

        // When
        val result = repository.getItemsByAccount(1).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Item", result[0].name)
        assertEquals(1, result[0].uqc)
        assertEquals(5, result[0].taxId)
    }

    @Test
    fun `syncItems calls API and inserts entities into DAO`() = runTest {
        // Given
        val dto = ItemDto(
            id = 1,
            name = "API Item",
            alt_name = null,
            brand = "API Brand",
            size = "Small",
            uqc = 2,
            hsn = 5678,
            account_id = 1,
            tax_id = 9,
            created_at = "2024-01-01",
            updated_at = "2024-01-01",
            deleted_at = null
        )
        val response = ItemsResponse(success = true, data = listOf(dto))
        
        coEvery { apiService.getItems(1) } returns response
        coEvery { itemDao.insertItems(any<List<ItemEntity>>()) } just Runs

        // When
        val result = repository.syncItems(1)

        // Then
        assertTrue(result is Result.Success)
        coVerify { itemDao.insertItems(any()) }
    }

    @Test
    fun `createItem calls API and inserts into DAO`() = runTest {
        // Given
        val dto = ItemDto(
            id = 100,
            name = "New Item",
            alt_name = "New Alt",
            brand = "New Brand",
            size = "M",
            uqc = 1,
            hsn = 9999,
            account_id = 1,
            tax_id = 5,
            created_at = "2024-01-01",
            updated_at = "2024-01-01",
            deleted_at = null
        )
        val response = ItemResponse(success = true, data = dto)

        coEvery { apiService.createItem(any()) } returns response
        coEvery { itemDao.insertItem(any()) } just Runs

        // When
        val result = repository.createItem(
            name = "New Item",
            altName = "New Alt",
            brand = "New Brand",
            size = "M",
            uqc = 1,
            hsn = 9999,
            accountId = 1,
            taxId = 5
        )

        // Then
        assert(result is Result.Success)
        assertEquals("New Item", (result as Result.Success).data.name)
        
        coVerify { 
            itemDao.insertItem(
                withArg { entity ->
                    assertEquals(100, entity.id)
                    assertEquals("New Item", entity.name)
                }
            )
        }
    }
}
