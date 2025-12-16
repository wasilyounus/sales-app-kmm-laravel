package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items") // Table for order items
data class OrderItemEntity(
    @PrimaryKey val id: Int,
    val orderId: Int,
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
