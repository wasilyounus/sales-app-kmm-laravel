package com.sales.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PriceList(
    val id: Long,
    val name: String,
    val itemsCount: Int = 0,
    val items: List<PriceListItem> = emptyList()
)
