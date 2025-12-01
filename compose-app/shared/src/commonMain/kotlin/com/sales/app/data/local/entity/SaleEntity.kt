package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val date: String,
    val invoiceNo: String,
    val accountId: Int,
    val taxId: Int? = null,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)

@Entity(tableName = "sale_items")
data class SaleItemEntity(
    @PrimaryKey val id: Int,
    val saleId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val taxId: Int?,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String
)
