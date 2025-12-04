package com.sales.app.presentation.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetPurchasesUseCase
import com.sales.app.domain.usecase.SyncPurchasesUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PurchasesViewModel(
    private val getPurchasesUseCase: GetPurchasesUseCase,
    private val syncPurchasesUseCase: SyncPurchasesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PurchasesUiState())
    val uiState: StateFlow<PurchasesUiState> = _uiState.asStateFlow()
    
    fun loadPurchases(accountId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            getPurchasesUseCase(accountId).collect { purchases ->
                val uiModels = purchases.map { purchase ->
                    PurchaseUiModel(
                        id = purchase.id,
                        partyName = "Party ${purchase.partyId}",
                        date = purchase.date,
                        itemsCount = purchase.items.size,
                        amount = purchase.items.sumOf { it.price * it.qty }.toString()
                    )
                }
                _uiState.update {
                    it.copy(
                        purchases = uiModels,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }
    
    fun onRefresh(accountId: Int) {
        _uiState.update { it.copy(isRefreshing = true) }
        
        viewModelScope.launch {
            when (val result = syncPurchasesUseCase(accountId)) {
                is Result.Success -> {
                    // Purchases will be updated via flow
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Loading -> {
                    // Loading state is already handled by isRefreshing flag
                }
            }
        }
    }
}
