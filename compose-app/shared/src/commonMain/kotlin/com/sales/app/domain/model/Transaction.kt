package com.sales.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Long,
    val date: String,
    val amount: Double,
    val type: Int,
    val debitCode: Int,
    val creditCode: Int,
    val comment: String?,
    val partyName: String? = null,
    val isReceived: Boolean = false
)
