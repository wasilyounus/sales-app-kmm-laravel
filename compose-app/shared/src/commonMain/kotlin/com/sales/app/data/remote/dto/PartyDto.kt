package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartyDto(
    val id: Int,
    val name: String,
    @SerialName("tax_number") val taxNumber: String?,
    val phone: String?,
    val email: String?,
    val account_id: Int,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val addresses: List<AddressDto>? = null
)

@Serializable
data class PartyRequest(
    val name: String,
    @SerialName("tax_number") val taxNumber: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val account_id: Int,
    val addresses: List<AddressRequest>? = null
)

@Serializable
data class PartiesResponse(
    val success: Boolean,
    val data: List<PartyDto>
)

@Serializable
data class PartyResponse(
    val success: Boolean,
    val data: PartyDto
)
