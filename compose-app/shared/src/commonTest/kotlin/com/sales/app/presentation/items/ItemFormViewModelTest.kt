package com.sales.app.presentation.items

import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Tax
import com.sales.app.domain.model.Uqc
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ItemFormViewModelTest {

    private lateinit var createItemUseCase: CreateItemUseCase
    private lateinit var updateItemUseCase: UpdateItemUseCase
    private lateinit var getItemByIdUseCase: GetItemByIdUseCase
    private lateinit var getUqcsUseCase: GetUqcsUseCase
    private lateinit var getTaxesUseCase: GetTaxesUseCase
    private lateinit var accountRepository: com.sales.app.domain.repository.AccountRepository
    
    private lateinit var viewModel: ItemFormViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        createItemUseCase = mockk(relaxed = true)
        updateItemUseCase = mockk(relaxed = true)
        getItemByIdUseCase = mockk(relaxed = true)
        getUqcsUseCase = mockk(relaxed = true)
        getTaxesUseCase = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        
        // Default mocks
        coEvery { getUqcsUseCase() } returns listOf(Uqc(1, "1", "Nos", "UQC", true))
        every { getTaxesUseCase() } returns flowOf(listOf(Tax(1, "GST", "India", "CGST", 9.0, "SGST", 9.0, null, null, null, null, true)))

        viewModel = ItemFormViewModel(
            createItemUseCase,
            updateItemUseCase,
            getItemByIdUseCase,
            getUqcsUseCase,
            getTaxesUseCase,
            accountRepository
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadItem populates state with item details`() = runTest {
        // Given
        val item = Item(
            id = 1,
            name = "Test Item",
            altName = "Alt",
            brand = "Brand",
            size = "L",
            uqc = 1,
            hsn = 1234,
            accountId = 1,
            taxId = 5
        )
        coEvery { getItemByIdUseCase(1, 1) } returns item
        every { getTaxesUseCase() } returns flowOf(emptyList())

        // When
        viewModel.loadItem(1, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Test Item", state.name)
        assertEquals("Brand", state.brand)
        assertEquals("L", state.size)
        assertEquals("1234", state.hsn)
        assertEquals(5, state.selectedTaxId)
        assertEquals(FormUiState.Update, state.formUiState)
    }

    @Test
    fun `saveItem calls createItemUseCase when adding`() = runTest {
        // Given
        viewModel.onNameChange("New Item")
        viewModel.onUqcChange(1)
        
        coEvery { createItemUseCase(any(), any(), any(), any(), any(), any(), any(), any()) } returns Result.Success(
            Item(1, "New Item", null, null, null, 1, null, 1)
        )

        // When
        viewModel.saveItem(1) {}
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { 
            createItemUseCase(
                accountId = 1,
                name = "New Item",
                altName = null,
                brand = null, // Empty string becomes null
                size = null,
                uqc = 1,
                hsn = null,
                taxId = null
            )
        }
    }
}
