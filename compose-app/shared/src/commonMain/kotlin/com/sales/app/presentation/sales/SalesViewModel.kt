package com.sales.app.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetSalesUseCase
import com.sales.app.domain.usecase.SyncSalesUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SalesViewModel(
    private val getSalesUseCase: GetSalesUseCase,
    private val syncSalesUseCase: SyncSalesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()
    
    fun loadSales(companyId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            getSalesUseCase(companyId).collect { sales ->
                val uiModels = sales.map { sale ->
                    SaleUiModel(
                        id = sale.id,
                        partyName = "Party ${sale.partyId}", // TODO: Fetch party name
                        date = sale.date,
                        invoiceNo = sale.invoiceNo,
                        itemsCount = sale.items.size,
                        amount = sale.items.sumOf { it.price * it.qty }.toString()
                    )
                }
                _uiState.update {
                    it.copy(
                        sales = uiModels,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }
    
    fun onRefresh(companyId: Int) {
        _uiState.update { it.copy(isRefreshing = true) }
        
        viewModelScope.launch {
            when (val result = syncSalesUseCase(companyId)) {
                is Result.Success -> {
                    // Sales will be updated via flow
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
