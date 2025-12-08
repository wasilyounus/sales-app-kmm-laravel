package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryNoteDto(
    val id: Int,
    val sale_id: Int,
    val dn_number: String? = null,
    val date: String,
    val vehicle_no: String? = null,
    val lr_no: String? = null,
    val notes: String? = null,
    val account_id: Int,
    val log_id: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val items: List<DeliveryNoteItemDto>? = null,
    val sale: SaleDto? = null
)

@Serializable
data class DeliveryNoteItemDto(
    val id: Int,
    val delivery_note_id: Int,
    val item_id: Int,
    val quantity: Double,
    val created_at: String? = null,
    val updated_at: String? = null,
    val item: ItemDto? = null
)

@Serializable
data class DeliveryNoteRequest(
    val sale_id: Int,
    val date: String,
    val vehicle_no: String? = null,
    val lr_no: String? = null,
    val notes: String? = null,
    val items: List<DeliveryNoteItemRequest>
)

@Serializable
data class DeliveryNoteItemRequest(
    val item_id: Int,
    val quantity: Double
)

@Serializable
data class DeliveryNotesResponse(
    val data: List<DeliveryNoteDto>
)

@Serializable
data class DeliveryNoteResponse(
    val message: String? = null,
    val data: DeliveryNoteDto
)
