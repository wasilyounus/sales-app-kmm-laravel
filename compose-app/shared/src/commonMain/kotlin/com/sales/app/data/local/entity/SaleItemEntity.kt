package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_items")
data class SaleItemEntity(
    @PrimaryKey val id: Int,
    val saleId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val taxId: Int?,
    val companyId: Int,
    val logId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
