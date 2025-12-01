package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    val name: String,
    @SerialName("name_formatted") val nameFormatted: String,
    val desc: String?,
    @SerialName("taxation_type") val taxationType: Int,
    @SerialName("tax_rate") val taxRate: Int,
    val gst: String?,
    val address: String?,
    val call: String?,
    val whatsapp: String?,
    @SerialName("footer_content") val footerContent: String?,
    val signature: Boolean?,
    @SerialName("financial_year_start") val financialYearStart: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class AccountResponse(
    val success: Boolean,
    val message: String? = null,
    val data: AccountDto? = null
)
