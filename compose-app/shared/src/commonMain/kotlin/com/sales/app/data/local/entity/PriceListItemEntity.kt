package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_list_items")
data class PriceListItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val priceListId: Long,
    val itemId: Long,
    val price: Double,
    val itemName: String? = null,
    val itemCode: String? = null,
    val standardPrice: Double? = null
)
