package com.sales.app.data.repository

import com.sales.app.data.local.dao.*
import com.sales.app.data.local.entity.SyncTimestampEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.*
import com.sales.app.domain.model.SyncType
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class SyncRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var itemDao: ItemDao
    private lateinit var partyDao: PartyDao
    private lateinit var addressDao: AddressDao
    private lateinit var taxDao: TaxDao
    private lateinit var uqcDao: UqcDao
    private lateinit var saleDao: SaleDao
    private lateinit var quoteDao: QuoteDao
    private lateinit var quoteItemDao: QuoteItemDao
    private lateinit var purchaseDao: PurchaseDao
    private lateinit var orderDao: OrderDao
    private lateinit var orderItemDao: OrderItemDao
    private lateinit var transactionDao: TransactionDao
    private lateinit var priceListDao: PriceListDao
    private lateinit var syncDao: SyncDao
    
    private lateinit var repository: SyncRepositoryImpl

    @BeforeTest
    fun setup() {
        apiService = mockk(relaxed = true)
        itemDao = mockk(relaxed = true)
        partyDao = mockk(relaxed = true)
        addressDao = mockk(relaxed = true)
        taxDao = mockk(relaxed = true)
        uqcDao = mockk(relaxed = true)
        saleDao = mockk(relaxed = true)
        quoteDao = mockk(relaxed = true)
        quoteItemDao = mockk(relaxed = true)
        purchaseDao = mockk(relaxed = true)
        orderDao = mockk(relaxed = true)
        orderItemDao = mockk(relaxed = true)
        transactionDao = mockk(relaxed = true)
        priceListDao = mockk(relaxed = true)
        syncDao = mockk(relaxed = true)

        repository = SyncRepositoryImpl(
            apiService, itemDao, partyDao, addressDao, taxDao, uqcDao,
            saleDao, quoteDao, quoteItemDao, purchaseDao, orderDao, orderItemDao,
            transactionDao, priceListDao, syncDao
        )
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `sync items calls API and inserts items`() = runTest {
        // Given
        val itemDto = ItemDto(
            id = 1,
            name = "Item 1",
            alt_name = "Alt 1",
            brand = "Brand",
            size = "M",
            uqc = 1,
            hsn = 123,
            account_id = 1,
            tax_id = 5,
            created_at = "2024-01-01",
            updated_at = "2024-01-01",
            deleted_at = null
        )
        val response = ItemsResponse(success = true, data = listOf(itemDto))
        
        coEvery { apiService.getItems(1) } returns response
        coEvery { itemDao.insertItems(any()) } just Runs

        // When
        val result = repository.sync(1, listOf(SyncType.Items))

        // Then
        assertTrue(result is Result.Success)
        coVerify { 
            itemDao.insertItems(
                withArg { entities ->
                    assertEquals(1, entities.size)
                    assertEquals("Item 1", entities[0].name)
                }
            )
        }
    }

    @Test
    fun `sync master data calls syncMasterData API`() = runTest {
        // Given
        val masterDataResponse = SyncResponse(
            success = true,
            data = SyncData(
                items = emptyList(),
                parties = emptyList(),
                taxes = emptyList(),
                uqcs = emptyList(),
                timestamp = "2024-01-02 00:00:00"
            )
        )
        
        coEvery { syncDao.getSyncTimestamp("master_data") } returns SyncTimestampEntity("master_data", "2024-01-01", 0)
        coEvery { apiService.syncMasterData(1, any()) } returns masterDataResponse
        coEvery { syncDao.insertSyncTimestamp(any()) } just Runs

        // When
        val result = repository.sync(1, listOf(SyncType.AllMasterData))

        // Then
        assertTrue(result is Result.Success)
        coVerify { apiService.syncMasterData(1, "2024-01-01") }
    }
}
