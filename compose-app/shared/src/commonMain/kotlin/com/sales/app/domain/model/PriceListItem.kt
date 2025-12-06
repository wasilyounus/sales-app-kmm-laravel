package com.sales.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PriceListItem(
    val id: Long,
    val priceListId: Long,
    val itemId: Long,
    val price: Double,
    val itemName: String? = null,
    val itemCode: String? = null,
    val standardPrice: Double? = null
)
