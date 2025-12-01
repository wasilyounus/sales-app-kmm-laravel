package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameFormatted: String,
    val desc: String?,
    val taxationType: Int,
    val taxRate: Int,
    val gst: String?,
    val address: String?,
    val call: String?,
    val whatsapp: String?,
    val footerContent: String?,
    val signature: String?,
    val financialYearStart: String?,
    val country: String? = "India",
    val state: String? = null,
    val taxNumber: String? = null,
    val defaultTaxId: Int? = null,
    val createdAt: String,
    val updatedAt: String
)
