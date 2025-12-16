package com.sales.app.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetItemsUseCase
import com.sales.app.domain.usecase.GetOrderByIdUseCase
import com.sales.app.domain.usecase.GetPartyByIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OrderViewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val order: OrderUiModel? = null,
    val items: List<OrderItemUiModel> = emptyList()
)

class OrderViewViewModel(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getPartyByIdUseCase: GetPartyByIdUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderViewUiState())
    val uiState: StateFlow<OrderViewUiState> = _uiState.asStateFlow()

    fun loadOrder(companyId: Int, orderId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                getOrderByIdUseCase(orderId),
                getItemsUseCase(companyId)
            ) { order, allItems ->
                if (order != null) {
                    val party = getPartyByIdUseCase(companyId, order.partyId).firstOrNull()
                    Triple(order, party, allItems)
                } else {
                    null
                }
            }.collect { triple ->
                if (triple != null) {
                    val (order, party, allItems) = triple
                    val partyName = party?.name ?: "Unknown Party"
                    
                    val items = order.items.map { item ->
                        val itemDetails = allItems.find { it.id == item.itemId }
                        OrderItemUiModel(
                            itemId = item.itemId,
                            itemName = itemDetails?.name ?: "Item #${item.itemId}",
                            price = item.price.toString(),
                            qty = item.qty.toString()
                        )
                    }
                    
                    val orderUiModel = OrderUiModel(
                        id = order.id,
                        orderNo = order.orderNo,
                        partyName = partyName,
                        date = order.date,
                        amount = order.items.sumOf { it.price * it.qty }.toString(),
                        itemsCount = order.items.size
                    )
                    
                    _uiState.update {
                        it.copy(
                            order = orderUiModel,
                            items = items,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Order not found")
                    }
                }
            }
        }
    }
}
