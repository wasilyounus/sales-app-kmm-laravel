package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val date: String,
    val accountId: Int,
    val logId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
