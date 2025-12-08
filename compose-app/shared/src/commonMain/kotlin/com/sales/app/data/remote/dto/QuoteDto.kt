package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    val id: Int,
    val party_id: Int,
    val date: String,
    val account_id: Int,
    val log_id: Int,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val items: List<QuoteItemDto>? = null // Items loaded from backend when requested
)

@Serializable
data class QuoteItemDto(
    val id: Int,
    val quote_id: Int,
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
data class QuoteRequest(
    val party_id: Int,
    val date: String,
    val account_id: Int,
    val items: List<QuoteItemRequest>
)

@Serializable
data class QuoteItemRequest(
    val id: Int = 0, // 0 for new items
    val item_id: Int,
    val price: Double,
    val qty: Double
)

@Serializable
data class QuotesResponse(
    val success: Boolean,
    val data: List<QuoteDto>
)

@Serializable
data class QuoteResponse(
    val success: Boolean,
    val data: QuoteDto
)

@Serializable
data class QuoteItemsResponse(
    val success: Boolean,
    val data: List<QuoteItemDto>
)
