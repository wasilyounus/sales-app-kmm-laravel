package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val success: Boolean,
    val data: SyncData
)

@Serializable
data class SyncData(
    val items: List<ItemDto>? = null,
    val parties: List<PartyDto>? = null,
    val uqcs: List<UqcDto>? = null,
    val taxes: List<TaxDto>? = null,
    val timestamp: String
)

@Serializable
data class SyncStatusResponse(
    val success: Boolean,
    val data: SyncStatus
)

@Serializable
data class SyncStatus(
    val items_count: Int,
    val parties_count: Int,
    val quotes_count: Int,
    val orders_count: Int,
    val sales_count: Int,
    val purchases_count: Int,
    val last_updated: String
)
