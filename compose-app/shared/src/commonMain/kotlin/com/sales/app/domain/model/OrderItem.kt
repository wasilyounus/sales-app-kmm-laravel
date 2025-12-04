package com.sales.app.domain.model

data class OrderItem(
    val id: Int,
    val orderId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val accountId: Int
)
