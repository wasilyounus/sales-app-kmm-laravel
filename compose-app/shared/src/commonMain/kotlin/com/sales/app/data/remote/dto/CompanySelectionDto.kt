package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountSelectionDto(
    val id: Int,
    val name: String,
    @SerialName("name_formatted") val nameFormatted: String,
    val role: String = "user",
    val visibility: String = "private"
)

@Serializable
data class AccountSelectionResponse(
    val accounts: List<AccountSelectionDto>
)

@Serializable
data class SelectAccountRequest(
    @SerialName("account_id") val accountId: Int
)

@Serializable
data class SelectAccountResponse(
    val message: String,
    @SerialName("account_id") val accountId: Int
)
