package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceListsResponse(
    val data: List<PriceListDto>,
    val current_page: Int,
    val last_page: Int
)

@Serializable
data class PriceListDto(
    val id: Long,
    val name: String,
    @SerialName("items_count") val itemsCount: Int = 0,
    val items: List<PriceListItemDto> = emptyList()
)

@Serializable
data class PriceListItemDto(
    val id: Long,
    @SerialName("price_list_id") val priceListId: Long,
    @SerialName("item_id") val itemId: Long,
    val price: Double,
    @SerialName("item_name") val itemName: String? = null,
    @SerialName("item_code") val itemCode: String? = null,
    @SerialName("standard_price") val standardPrice: Double? = null
)

@Serializable
data class PriceListRequest(
    val name: String
)

@Serializable
data class UpdatePriceListItemsRequest(
    val items: List<PriceListItemUpdateDto>
)

@Serializable
data class PriceListItemUpdateDto(
    @SerialName("item_id") val itemId: Long,
    val price: Double
)
