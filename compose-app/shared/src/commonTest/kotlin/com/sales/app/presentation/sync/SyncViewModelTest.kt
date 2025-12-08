package com.sales.app.presentation.sync

import com.sales.app.domain.model.SyncType
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*


@OptIn(ExperimentalCoroutinesApi::class)
class SyncViewModelTest {
    
    private lateinit var syncMasterDataUseCase: SyncMasterDataUseCase
    private lateinit var fullSyncUseCase: FullSyncUseCase
    private lateinit var syncDataUseCase: SyncDataUseCase
    private lateinit var viewModel: SyncViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        syncMasterDataUseCase = mockk()
        fullSyncUseCase = mockk()
        syncDataUseCase = mockk()
        viewModel = SyncViewModel(syncDataUseCase, syncMasterDataUseCase, fullSyncUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `syncData should update isSyncing state`() = runTest {
        // Given
        val accountId = 1
        val types = listOf(SyncType.Items)
        coEvery { syncDataUseCase(accountId, types) } coAnswers {
            delay(100)
            Result.Success(Unit)
        }
        
        // When
        assertFalse(viewModel.uiState.value.isSyncing)
        viewModel.syncData(accountId, types)
        
        // Run current tasks to start the coroutine
        testDispatcher.scheduler.runCurrent()
        
        // Then - immediately after start
        assertTrue(viewModel.uiState.value.isSyncing)
        
        // Advance time to complete
        testDispatcher.scheduler.advanceUntilIdle()
        
        // After completion
        assertFalse(viewModel.uiState.value.isSyncing)
    }
    
    @Test
    fun `syncData success should set success message`() = runTest {
        // Given
        val accountId = 1
        val types = listOf(SyncType.Sales)
        coEvery { syncDataUseCase(accountId, types) } returns Result.Success(Unit)
        
        // When
        viewModel.syncData(accountId, types)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertNotNull(viewModel.uiState.value.successMessage)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `syncData failure should set error message`() = runTest {
        // Given
        val accountId = 1
        val types = listOf(SyncType.Quotes)
        val errorMessage = "Network failed"
        coEvery { syncDataUseCase(accountId, types) } returns Result.Error(errorMessage)
        
        // When
        viewModel.syncData(accountId, types)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.successMessage)
    }
    
    @Test
    fun `syncMasterData should call syncMasterDataUseCase`() = runTest {
        // Given
        val accountId = 1
        coEvery { syncMasterDataUseCase(accountId) } returns Result.Success(Unit)
        
        // When
        viewModel.syncMasterData(accountId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { syncMasterDataUseCase(accountId) }
    }
    
    @Test
    fun `fullSync should call fullSyncUseCase`() = runTest {
        // Given
        val accountId = 1
        coEvery { fullSyncUseCase(accountId) } returns Result.Success(Unit)
        
        // When
        viewModel.fullSync(accountId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { fullSyncUseCase(accountId) }
    }
}
