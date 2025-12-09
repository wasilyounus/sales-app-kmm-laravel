package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val date: String,

    val invoiceNo: String? = null,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)


