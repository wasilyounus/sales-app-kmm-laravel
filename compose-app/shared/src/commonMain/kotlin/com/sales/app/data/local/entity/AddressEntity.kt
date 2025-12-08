package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: Int,
    val partyId: Int,
    val accountId: Int,
    val line1: String,
    val line2: String?,
    val place: String,
    val district: String?,
    val state: String,
    val pincode: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: String,
    val updatedAt: String
)
