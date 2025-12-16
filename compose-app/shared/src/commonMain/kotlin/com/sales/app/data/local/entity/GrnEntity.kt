package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grns")
data class GrnEntity(
    @PrimaryKey val id: Int,
    val purchaseId: Int,
    val grnNo: String?,
    val date: String,
    val vehicleNo: String?,
    val invoiceNo: String?,
    val notes: String?,
    val companyId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
