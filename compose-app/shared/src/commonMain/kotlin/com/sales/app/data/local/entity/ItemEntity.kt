package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val altName: String?,
    val brand: String?,
    val size: String?,
    val uqc: Int,
    val hsn: Int?,
    val companyId: Int,
    val taxId: Int? = null,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
