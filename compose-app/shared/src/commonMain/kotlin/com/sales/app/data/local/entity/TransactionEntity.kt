package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val date: String,
    val amount: Double,
    val type: Int,
    val debitCode: Int,
    val creditCode: Int,
    val comment: String?,
    val companyId: Int,
    val partyName: String? = null,
    val isReceived: Boolean = false
)
