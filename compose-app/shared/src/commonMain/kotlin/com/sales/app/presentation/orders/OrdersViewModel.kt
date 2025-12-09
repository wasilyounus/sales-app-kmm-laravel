package com.sales.app.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetOrdersUseCase
import com.sales.app.domain.usecase.SyncOrdersUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val syncOrdersUseCase: SyncOrdersUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    
    fun loadOrders(accountId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            getOrdersUseCase(accountId).collect { orders ->
                val uiModels = orders.map { order ->
                    OrderUiModel(
                        id = order.id,
                        orderNo = order.orderNo,
                        partyName = "Party ${order.partyId}",
                        date = order.date,
                        itemsCount = order.items.size,
                        amount = order.items.sumOf { it.price * it.qty }.toString()
                    )
                }
                _uiState.update {
                    it.copy(
                        orders = uiModels,
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
            when (val result = syncOrdersUseCase(accountId)) {
                is Result.Success -> {
                    // Orders will be updated via flow
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
