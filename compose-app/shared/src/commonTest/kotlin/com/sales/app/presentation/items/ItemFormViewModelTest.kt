package com.sales.app.presentation.items

import com.sales.app.domain.model.Tax
import com.sales.app.domain.model.Uqc
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ItemFormViewModelTest {

    private lateinit var createItemUseCase: CreateItemUseCase
    private lateinit var updateItemUseCase: UpdateItemUseCase
    private lateinit var getItemByIdUseCase: GetItemByIdUseCase
    private lateinit var getUqcsUseCase: GetUqcsUseCase
    private lateinit var getTaxesUseCase: GetTaxesUseCase
    private lateinit var viewModel: ItemFormViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        createItemUseCase = mockk()
        updateItemUseCase = mockk()
        getItemByIdUseCase = mockk()
        getUqcsUseCase = mockk()
        getTaxesUseCase = mockk()

        // Mock default UQCs
        coEvery { getUqcsUseCase() } returns listOf(
            Uqc(1, "KG", "Kilogram", "Weight", true)
        )

        // Mock default taxes
        every { getTaxesUseCase() } returns flowOf(
            listOf(
                Tax(1, "GST 18%", "CGST", 9.0, "SGST", 9.0, null, null, null, null, true),
                Tax(2, "GST 5%", "CGST", 2.5, "SGST", 2.5, null, null, null, null, true)
            )
        )

        viewModel = ItemFormViewModel(
            createItemUseCase,
            updateItemUseCase,
            getItemByIdUseCase,
            getUqcsUseCase,
            getTaxesUseCase
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state has no tax selected`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertNull(state.selectedTaxId)
    }

    @Test
    fun `taxes are loaded on init`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(2, state.taxes.size)
        assertEquals("GST 18%", state.taxes[0].name)
        assertEquals("GST 5%", state.taxes[1].name)
    }

    @Test
    fun `onTaxChange updates selected tax`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onTaxChange(1)
        
        val state = viewModel.uiState.value
        assertEquals(1, state.selectedTaxId)
    }

    @Test
    fun `onTaxChange can set tax to null`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onTaxChange(1)
        assertEquals(1, viewModel.uiState.value.selectedTaxId)
        
        viewModel.onTaxChange(null)
        assertNull(viewModel.uiState.value.selectedTaxId)
    }

    @Test
    fun `saveItem includes selectedTaxId`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Setup
        viewModel.onNameChange("Test Item")
        viewModel.onTaxChange(1)
        
        coEvery {
            createItemUseCase(
                accountId = any(),
                name = any(),
                altName = any(),
                brand = any(),
                size = any(),
                uqc = any(),
                hsn = any(),
                taxId = 1
            )
        } returns Result.Success(mockk(relaxed = true))

        // Execute
        var callbackCalled = false
        viewModel.saveItem(accountId = 1, onSuccess = { callbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify
        coVerify {
            createItemUseCase(
                accountId = 1,
                name = "Test Item",
                altName = null,
                brand = null,
                size = null,
                uqc = 1,
                hsn = null,
                taxId = 1
            )
        }
        assertTrue(callbackCalled)
    }

    @Test
    fun `saveItem with null tax sends null taxId`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onNameChange("Test Item")
        // Don't set any tax (remains null)
        
        coEvery {
            createItemUseCase(
                accountId = any(),
                name = any(),
                altName = any(),
                brand = any(),
                size = any(),
                uqc = any(),
                hsn = any(),
                taxId = null
            )
        } returns Result.Success(mockk(relaxed = true))

        viewModel.saveItem(accountId = 1, onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            createItemUseCase(
                accountId = any(),
                name = any(),
                altName = any(),
                brand = any(),
                size = any(),
                uqc = any(),
                hsn = any(),
                taxId = null
            )
        }
    }
}
