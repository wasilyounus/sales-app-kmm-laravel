package com.sales.app.domain.model

data class Order(
    val id: Int,
    val partyId: Int,
    val date: String,
    val orderNo: String? = null,
    val companyId: Int,
    val items: List<OrderItem> = emptyList()
)
