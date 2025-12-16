package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_lists")
data class PriceListEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val companyId: Int,
    val itemsCount: Int = 0
)
