package com.sales.app.domain.model

data class PurchaseItem(
    val id: Int,
    val purchaseId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val taxId: Int?,
    val accountId: Int
)
