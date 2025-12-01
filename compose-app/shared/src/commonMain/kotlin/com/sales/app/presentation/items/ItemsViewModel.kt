package com.sales.app.presentation.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetItemsUseCase
import com.sales.app.domain.usecase.GetUqcsUseCase
import com.sales.app.domain.usecase.SyncMasterDataUseCase
import com.sales.app.presentation.items.model.ItemUiModel
import com.sales.app.presentation.items.model.search
import com.sales.app.presentation.items.model.toUiModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ItemsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<ItemUiModel> = emptyList(),
    val allItems: List<ItemUiModel> = emptyList(), // Unfiltered list for search
    val error: String? = null,
    val searchQuery: String = ""
)

class ItemsViewModel(
    private val getItemsUseCase: GetItemsUseCase,
    private val syncMasterDataUseCase: SyncMasterDataUseCase,
    private val getUqcsUseCase: GetUqcsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ItemsUiState())
    val uiState: StateFlow<ItemsUiState> = _uiState.asStateFlow()
    
    fun loadItems(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Trigger sync in background
            launch {
                try {
                    syncMasterDataUseCase(accountId)
                } catch (e: Exception) {
                    // Ignore sync errors for now, just show local data
                    e.printStackTrace()
                }
            }
            
            // Load UQCs first
            val uqcs = try {
                getUqcsUseCase()
            } catch (e: Exception) {
                emptyList()
            }
            val uqcMap = uqcs.associate { it.id to it.uqc }
            
            getItemsUseCase(accountId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = e.message ?: "Unknown error"
                        ) 
                    }
                }
                .collect { items ->
                    val uiModels = items.map { item ->
                        item.toUiModel(uqcName = uqcMap[item.uqc] ?: "UQC")
                    }
                    
                    val filteredItems = if (_uiState.value.searchQuery.isNotBlank()) {
                        uiModels.search(_uiState.value.searchQuery)
                    } else {
                        uiModels
                    }
                    
                    _uiState.update { 
                        it.copy(
                            allItems = uiModels,
                            items = filteredItems,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        ) 
                    }
                }
        }
    }
    
    fun onRefresh(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadItems(accountId)
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filteredItems = if (query.isNotBlank()) {
                state.allItems.search(query)
            } else {
                state.allItems
            }
            state.copy(
                searchQuery = query,
                items = filteredItems
            )
        }
    }
    
    fun onDeleteItem(itemId: Int) {
        // TODO: Implement delete functionality
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    allItems = state.allItems.filter { it.itemId != itemId },
                    items = state.items.filter { it.itemId != itemId }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
