package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taxes")
data class TaxEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val tax1Name: String?,
    val tax1Rate: Double?,
    val tax2Name: String?,
    val tax2Rate: Double?,
    val tax3Name: String?,
    val tax3Rate: Double?,
    val tax4Name: String?,
    val tax4Rate: Double?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
