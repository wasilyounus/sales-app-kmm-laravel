package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SaleDto(
    val id: Int,
    val party_id: Int,
    val date: String,
    val invoice_no: String,
    val tax_id: Int? = null,
    val account_id: Int,
    val log_id: Int,
    val deleted_at: String? = null,
    val items: List<SaleItemDto>? = null
)

@Serializable
data class SaleItemDto(
    val id: Int,
    val sale_id: Int,
    val item_id: Int,
    val price: Double,
    val qty: Double,
    val tax_id: Int? = null,
    val account_id: Int,
    val log_id: Int,
    val deleted_at: String? = null
)

@Serializable
data class SaleRequest(
    val party_id: Int,
    val date: String,
    val invoice_no: String,
    val tax_id: Int? = null,
    val account_id: Int,
    val items: List<SaleItemRequest>
)

@Serializable
data class SaleItemRequest(
    val id: Int = 0,
    val item_id: Int,
    val price: Double,
    val qty: Double,
    val tax_id: Int? = null
)

@Serializable
data class SalesResponse(
    val success: Boolean,
    val data: List<SaleDto>
)

@Serializable
data class SaleResponse(
    val success: Boolean,
    val data: SaleDto
)

@Serializable
data class SaleItemsResponse(
    val success: Boolean,
    val data: List<SaleItemDto>
)
