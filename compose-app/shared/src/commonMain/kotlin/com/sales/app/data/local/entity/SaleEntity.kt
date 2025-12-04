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

