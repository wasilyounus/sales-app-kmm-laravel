package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_items")
data class PurchaseItemEntity(
    @PrimaryKey val id: Int,
    val purchaseId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val accountId: Int,
    val logId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
