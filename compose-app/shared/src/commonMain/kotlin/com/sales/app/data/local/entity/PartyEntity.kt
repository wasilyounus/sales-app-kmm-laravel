package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val gst: String?,
    val phone: String?,
    val email: String?,
    val accountId: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)
