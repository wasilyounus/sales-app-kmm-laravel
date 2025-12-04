package com.sales.app.presentation.sales

data class SaleUiModel(
    val id: Int,
    val partyName: String,
    val date: String,
    val invoiceNo: String,
    val itemsCount: Int,
    val amount: String
)

data class SaleItemUiModel(
    val itemId: Int,
    val itemName: String,
    val price: String,
    val qty: String,
    val taxId: Int?
)

data class SalesUiState(
    val sales: List<SaleUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
