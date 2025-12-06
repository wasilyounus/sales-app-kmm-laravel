package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponse(
    val data: List<TransactionDto>,
    val current_page: Int,
    val last_page: Int
)

@Serializable
data class TransactionDto(
    val id: Long,
    val date: String,
    val amount: Double,
    val type: Int,
    @SerialName("debit_code") val debitCode: Int,
    @SerialName("credit_code") val creditCode: Int,
    val comment: String?,
    @SerialName("party_name") val partyName: String? = null,
    @SerialName("is_received") val isReceived: Boolean = false
)

@Serializable
data class TransactionRequest(
    val date: String,
    val amount: Double,
    val type: String, // "received" or "paid"
    val method: String, // "cash", "cheque", "upi", "neft"
    @SerialName("party_id") val partyId: Int,
    val comment: String? = null
)
