package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taxes")
data class TaxEntity(
    @PrimaryKey val id: Int,
    val schemeName: String,
    val country: String? = null,
    val tax1Name: String?,
    val tax1Val: Double?,
    val tax2Name: String?,
    val tax2Val: Double?,
    val tax3Name: String?,
    val tax3Val: Double?,
    val tax4Name: String?,
    val tax4Val: Double?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

