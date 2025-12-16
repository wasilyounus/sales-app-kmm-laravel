package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanySelectionDto(
    val id: Int,
    val name: String,
    @SerialName("name_formatted") val nameFormatted: String,
    val role: String = "user",
    val visibility: String = "private"
)

@Serializable
data class CompanySelectionResponse(
    @SerialName("companies") val companies: List<CompanySelectionDto>
)

@Serializable
data class SelectCompanyRequest(
    @SerialName("company_id") val companyId: Int
)

@Serializable
data class SelectCompanyResponse(
    val message: String,
    @SerialName("company_id") val companyId: Int
)
