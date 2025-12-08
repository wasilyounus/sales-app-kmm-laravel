package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery_notes")
data class DeliveryNoteEntity(
    @PrimaryKey val id: Int,
    val saleId: Int,
    val dnNumber: String?,
    val date: String,
    val vehicleNo: String?,
    val lrNo: String?,
    val notes: String?,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
