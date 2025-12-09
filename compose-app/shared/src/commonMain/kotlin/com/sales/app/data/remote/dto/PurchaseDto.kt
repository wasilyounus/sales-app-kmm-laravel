package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDto(
    val id: Int,
    val party_id: Int,
    val date: String,
    val account_id: Int,
    val log_id: Int,
    val invoice_no: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val items: List<PurchaseItemDto>? = null
)

@Serializable
data class PurchaseItemDto(
    val id: Int,
    val purchase_id: Int,
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
data class PurchaseRequest(
    val party_id: Int,
    val date: String,
    val invoice_no: String? = null,
    val account_id: Int,
    val items: List<PurchaseItemRequest>
)

@Serializable
data class PurchaseItemRequest(
    val id: Int = 0,
    val item_id: Int,
    val price: Double,
    val qty: Double,
    val tax_id: Int? = null
)

@Serializable
data class PurchasesResponse(
    val success: Boolean,
    val data: List<PurchaseDto>
)

@Serializable
data class PurchaseResponse(
    val success: Boolean,
    val data: PurchaseDto
)

@Serializable
data class PurchaseItemsResponse(
    val success: Boolean,
    val data: List<PurchaseItemDto>
)
