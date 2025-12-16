package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GrnDto(
    val id: Int,
    val purchase_id: Int,
    val grn_no: String? = null,
    val date: String,
    val vehicle_no: String? = null,
    val invoice_no: String? = null,
    val notes: String? = null,
    val company_id: Int,
    val log_id: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val items: List<GrnItemDto>? = null,
    val purchase: PurchaseDto? = null
)

@Serializable
data class GrnItemDto(
    val id: Int,
    val grn_id: Int,
    val item_id: Int,
    val quantity: Double,
    val created_at: String? = null,
    val updated_at: String? = null,
    val item: ItemDto? = null
)

@Serializable
data class GrnRequest(
    val purchase_id: Int,
    val date: String,
    val vehicle_no: String? = null,
    val invoice_no: String? = null,
    val notes: String? = null,
    val items: List<GrnItemRequest>
)

@Serializable
data class GrnItemRequest(
    val item_id: Int,
    val quantity: Double
)

@Serializable
data class GrnsResponse(
    val data: List<GrnDto>
)

@Serializable
data class GrnResponse(
    val message: String? = null,
    val data: GrnDto
)
