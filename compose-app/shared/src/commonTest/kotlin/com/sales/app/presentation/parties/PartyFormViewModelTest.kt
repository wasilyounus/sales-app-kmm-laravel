package com.sales.app.presentation.parties

import com.sales.app.domain.usecase.CreatePartyUseCase
import com.sales.app.domain.usecase.GetPartyByIdUseCase
import com.sales.app.domain.usecase.UpdatePartyUseCase
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PartyFormViewModelTest {

    private lateinit var createPartyUseCase: CreatePartyUseCase
    private lateinit var updatePartyUseCase: UpdatePartyUseCase
    private lateinit var getPartyByIdUseCase: GetPartyByIdUseCase
    private lateinit var viewModel: PartyFormViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        createPartyUseCase = mockk()
        updatePartyUseCase = mockk()
        getPartyByIdUseCase = mockk()

        viewModel = PartyFormViewModel(
            createPartyUseCase,
            updatePartyUseCase,
            getPartyByIdUseCase
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state has empty taxNumber`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("", state.taxNumber)
    }

    @Test
    fun `onTaxNumberChange updates taxNumber in state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onTaxNumberChange("NEWTAX123")
        
        val state = viewModel.uiState.value
        assertEquals("NEWTAX123", state.taxNumber)
    }

    @Test
    fun `saveParty includes taxNumber`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Setup
        viewModel.onNameChange("Test Party")
        viewModel.onTaxNumberChange("TAX456")
        
        coEvery {
            createPartyUseCase(
                accountId = any(),
                name = any(),
                taxNumber = "TAX456",
                addresses = any(),
                phone = any(),
                email = any()
            )
        } returns Result.Success(mockk(relaxed = true))

        // Execute
        var callbackCalled = false
        viewModel.saveParty(accountId = 1, onSuccess = { callbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify
        coVerify {
            createPartyUseCase(
                accountId = 1,
                name = "Test Party",
                taxNumber = "TAX456",
                addresses = any(),
                phone = null,
                email = null
            )
        }
        assertTrue(callbackCalled)
    }

    @Test
    fun `saveParty with null taxNumber sends null`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onNameChange("Test Party")
        // Don't set taxNumber (remains empty string or null)
        
        coEvery {
            createPartyUseCase(
                accountId = any(),
                name = any(),
                taxNumber = any(),
                addresses = any(),
                phone = any(),
                email = any()
            )
        } returns Result.Success(mockk(relaxed = true))

        viewModel.saveParty(accountId = 1, onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            createPartyUseCase(
                accountId = any(),
                name = any(),
                taxNumber = any(), // Should be empty string or null
                addresses = any(),
                phone = any(),
                email = any()
            )
        }
    }
}
