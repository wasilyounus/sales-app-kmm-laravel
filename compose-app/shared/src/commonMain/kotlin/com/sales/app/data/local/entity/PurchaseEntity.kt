package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val date: String,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)

@Entity(tableName = "purchase_items")
data class PurchaseItemEntity(
    @PrimaryKey val id: Int,
    val purchaseId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val taxId: Int?,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String
)
