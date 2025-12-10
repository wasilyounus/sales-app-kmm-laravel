package com.sales.app.presentation.purchases

import com.sales.app.data.remote.dto.PurchaseItemRequest
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Party
import com.sales.app.domain.model.Purchase
import com.sales.app.domain.model.PurchaseItem
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PurchaseFormViewModelTest {

    private lateinit var createPurchaseUseCase: CreatePurchaseUseCase
    private lateinit var getPurchaseByIdUseCase: GetPurchaseByIdUseCase
    private lateinit var updatePurchaseUseCase: UpdatePurchaseUseCase
    private lateinit var getPartiesUseCase: GetPartiesUseCase
    private lateinit var getItemsUseCase: GetItemsUseCase
    
    private lateinit var viewModel: PurchaseFormViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        createPurchaseUseCase = mockk(relaxed = true)
        getPurchaseByIdUseCase = mockk(relaxed = true)
        updatePurchaseUseCase = mockk(relaxed = true)
        getPartiesUseCase = mockk(relaxed = true)
        getItemsUseCase = mockk(relaxed = true)
        
        // Default mocks
        every { getPartiesUseCase(any()) } returns flowOf(emptyList())
        every { getItemsUseCase(any()) } returns flowOf(emptyList())

        viewModel = PurchaseFormViewModel(
            createPurchaseUseCase,
            getPurchaseByIdUseCase,
            updatePurchaseUseCase,
            getPartiesUseCase,
            getItemsUseCase
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `onInvoiceNoChange updates state with user input`() {
        // When
        viewModel.onInvoiceNoChange("INV-MANUAL-001")

        // Then
        assertEquals("INV-MANUAL-001", viewModel.uiState.value.invoiceNo)
    }

    @Test
    fun `savePurchase calls createPurchaseUseCase with invoiceNo`() = runTest {
        // Given
        val partyId = 1
        val accountId = 1
        val date = "2025-12-09"
        
        viewModel.onPartyChange(partyId)
        viewModel.onDateChange(date)
        viewModel.onInvoiceNoChange("custom-inv-123")
        
        // Add an item so form is valid
        val item = Item(1, "Item 1", null, null, null, 1, null, 1)
        viewModel.onAddItem(item, 10.0, 100.0)
        
        coEvery { createPurchaseUseCase(any(), any(), any(), any(), any()) } returns Result.Success(
            Purchase(1, partyId, date, "custom-inv-123", accountId, emptyList())
        )

        // When
        viewModel.savePurchase(accountId) {}
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { 
            createPurchaseUseCase(
                partyId = partyId,
                date = date,
                items = any(),
                accountId = accountId,
                invoiceNo = "custom-inv-123" // Verifying manual invoice passing
            )
        }
    }
    
    @Test
    fun `updatePurchase calls updatePurchaseUseCase with invoiceNo`() = runTest {
        // Given
        val purchaseId = 1
        val accountId = 1
        val existingPurchase = Purchase(
            id = purchaseId,
            partyId = 1,
            date = "2025-12-01",
            invoiceNo = "OLD-INV",
            accountId = accountId,
            items = listOf(PurchaseItem(1, 1, 1, 100.0, 5.0, 1, accountId))
        )
        
        every { getPurchaseByIdUseCase(purchaseId) } returns flowOf(existingPurchase)
        
        // Load Data
        viewModel.loadData(accountId, purchaseId)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for load
        
        // Change Invoice No
        viewModel.onInvoiceNoChange("NEW-INV-UPDATED")
        
        coEvery { updatePurchaseUseCase(any(), any(), any(), any(), any(), any()) } returns Result.Success(
            existingPurchase.copy(invoiceNo = "NEW-INV-UPDATED")
        )

        // When
        viewModel.savePurchase(accountId) {}
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify {
            updatePurchaseUseCase(
                id = purchaseId,
                partyId = 1,
                date = "2025-12-01",
                items = any(),
                accountId = accountId,
                invoiceNo = "NEW-INV-UPDATED" // Verifying updated invoice passing
            )
        }
    }
}
