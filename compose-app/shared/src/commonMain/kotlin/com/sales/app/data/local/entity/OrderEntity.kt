package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val date: String,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: Int,
    val orderId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String
)
