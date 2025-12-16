package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["companyId"])]
)
data class ContactEntity(
    @PrimaryKey val id: Int,
    val companyId: Int,
    val name: String,
    val phone: String?,
    val email: String?,
    val designation: String?,
    val isPrimary: Boolean
)
