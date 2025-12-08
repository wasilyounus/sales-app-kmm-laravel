package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery_note_items")
data class DeliveryNoteItemEntity(
    @PrimaryKey val id: Int,
    val deliveryNoteId: Int,
    val itemId: Int,
    val quantity: Double,
    val createdAt: String,
    val updatedAt: String
)
