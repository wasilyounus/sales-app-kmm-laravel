package com.sales.app.presentation.purchases

data class PurchaseUiModel(
    val id: Int,
    val partyName: String,
    val date: String,
    val itemsCount: Int,
    val amount: String
)

data class PurchaseItemUiModel(
    val itemId: Int,
    val itemName: String,
    val price: String,
    val qty: String,
    val taxId: Int?
)

data class PurchasesUiState(
    val purchases: List<PurchaseUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
