package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: Int,
    val party_id: Int,
    val date: String,
    val account_id: Int,
    val log_id: Int,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val items: List<OrderItemDto>? = null
)

@Serializable
data class OrderItemDto(
    val id: Int,
    val order_id: Int,
    val item_id: Int,
    val price: Double,
    val qty: Double,
    val tax_id: Int? = null,
    val account_id: Int,
    val log_id: Int,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null
)

@Serializable
data class OrderRequest(
    val party_id: Int,
    val date: String,
    val account_id: Int,
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    val id: Int = 0,
    val item_id: Int,
    val price: Double,
    val qty: Double
)

@Serializable
data class OrdersResponse(
    val success: Boolean,
    val data: List<OrderDto>
)

@Serializable
data class OrderResponse(
    val success: Boolean,
    val data: OrderDto
)

@Serializable
data class OrderItemsResponse(
    val success: Boolean,
    val data: List<OrderItemDto>
)
