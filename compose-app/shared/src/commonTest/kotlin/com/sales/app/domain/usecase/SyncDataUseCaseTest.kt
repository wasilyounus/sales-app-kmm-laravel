package com.sales.app.domain.usecase

import com.sales.app.domain.model.SyncType
import com.sales.app.domain.repository.SyncRepository
import com.sales.app.util.Result
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class SyncDataUseCaseTest {
    
    private lateinit var syncRepository: SyncRepository
    private lateinit var syncDataUseCase: SyncDataUseCase
    
    @Before
    fun setup() {
        syncRepository = mockk()
        syncDataUseCase = SyncDataUseCase(syncRepository)
    }
    
    @Test
    fun `invoke should delegate to repository sync method`() = runTest {
        // Given
        val accountId = 123
        val types = listOf(SyncType.Items, SyncType.Sales)
        coEvery { syncRepository.sync(accountId, types) } returns Result.Success(Unit)
        
        // When
        val result = syncDataUseCase(accountId, types)
        
        // Then
        assertTrue(result is Result.Success)
        coVerify { syncRepository.sync(accountId, types) }
    }
    
    @Test
    fun `invoke should propagate errors from repository`() = runTest {
        // Given
        val accountId = 123
        val types = listOf(SyncType.Everything)
        val error = Exception("Network error")
        coEvery { syncRepository.sync(accountId, types) } returns Result.Error("Sync failed", error)
        
        // When
        val result = syncDataUseCase(accountId, types)
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals("Sync failed", (result as Result.Error).message)
    }
}
