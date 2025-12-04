package com.sales.app.domain.model

data class SaleItem(
    val id: Int,
    val saleId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val taxId: Int?,
    val accountId: Int
)
