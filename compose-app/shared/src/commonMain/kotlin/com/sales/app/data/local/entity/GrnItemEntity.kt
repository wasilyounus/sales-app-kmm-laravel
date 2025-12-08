package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grn_items")
data class GrnItemEntity(
    @PrimaryKey val id: Int,
    val grnId: Int,
    val itemId: Int,
    val quantity: Double,
    val createdAt: String,
    val updatedAt: String
)
