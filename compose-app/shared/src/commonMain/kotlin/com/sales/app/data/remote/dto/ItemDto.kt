package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: Int,
    val name: String,
    val alt_name: String? = null,
    val brand: String? = null,
    val size: String? = null,
    val uqc: Int,
    val hsn: Int? = null,
    val account_id: Int,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String? = null
)

@Serializable
data class ItemRequest(
    val name: String,
    val alt_name: String? = null,
    val brand: String? = null,
    val size: String? = null,
    val uqc: Int,
    val hsn: Int? = null,
    val account_id: Int,
    val log_id: Int = 0 // Default to 0 or handle in repository
)

@Serializable
data class ItemsResponse(
    val success: Boolean,
    val data: List<ItemDto>
)

@Serializable
data class ItemResponse(
    val success: Boolean,
    val data: ItemDto
)

@Serializable
data class UqcDto(
    val id: Int,
    val uqc: String,
    val quantity: String? = null,
    val type: String? = null,
    val active: Boolean
)

@Serializable
data class UqcsResponse(
    val success: Boolean,
    val data: List<UqcDto>
)

@Serializable
data class TaxDto(
    val id: Int,
    val name: String,
    val tax1_name: String?,
    val tax1_rate: Double?,
    val tax2_name: String?,
    val tax2_rate: Double?,
    val tax3_name: String?,
    val tax3_rate: Double?,
    val tax4_name: String?,
    val tax4_rate: Double?,
    val active: Boolean
)
