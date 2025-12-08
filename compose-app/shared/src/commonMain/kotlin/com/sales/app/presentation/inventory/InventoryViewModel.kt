package com.sales.app.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.InventorySummary
import com.sales.app.domain.usecase.GetInventorySummaryUseCase
import com.sales.app.domain.usecase.SyncMasterDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<InventorySummary> = emptyList(),
    val error: String? = null
)

class InventoryViewModel(
    private val getInventorySummaryUseCase: GetInventorySummaryUseCase,
    private val syncMasterDataUseCase: SyncMasterDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    fun loadInventory(accountId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Trigger sync in background
            launch {
                try {
                    syncMasterDataUseCase(accountId)
                } catch (e: Exception) {
                    // Ignore sync errors, just show local data
                    e.printStackTrace()
                }
            }
            
            try {
                getInventorySummaryUseCase(accountId).collect { items ->
                    _uiState.update { 
                        it.copy(
                            items = items,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun onRefresh(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadInventory(accountId)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
