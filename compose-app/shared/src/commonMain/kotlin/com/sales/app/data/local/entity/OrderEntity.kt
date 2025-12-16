package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val date: String,

    val orderNo: String? = null,
    val companyId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)


