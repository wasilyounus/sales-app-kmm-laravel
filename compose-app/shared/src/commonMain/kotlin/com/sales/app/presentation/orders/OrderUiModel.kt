package com.sales.app.presentation.orders

data class OrderUiModel(
    val id: Int,
    val partyName: String,
    val date: String,
    val itemsCount: Int,
    val amount: String
)

data class OrderItemUiModel(
    val itemId: Int,
    val itemName: String,
    val price: String,
    val qty: String
)

data class OrdersUiState(
    val orders: List<OrderUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
