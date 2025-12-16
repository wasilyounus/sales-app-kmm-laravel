package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemId: Int,
    val qty: Double,
    val type: String, // "IN" or "OUT"
    val date: String,
    val referenceId: Int?,
    val referenceType: String?,
    val reason: String?,
    val companyId: Int,
    val createdAt: String,
    val updatedAt: String
)
