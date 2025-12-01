package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "uqcs")
data class UqcEntity(
    @PrimaryKey val id: Int,
    val code: String,
    val name: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
