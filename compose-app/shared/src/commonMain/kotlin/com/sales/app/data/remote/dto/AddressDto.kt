package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: Int,
    val party_id: Int,
    val account_id: Int,
    val line1: String,
    val line2: String? = null,
    val place: String,
    val district: String? = null,
    val state: String,
    val pincode: String,
    val country: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class AddressRequest(
    val line1: String,
    val line2: String? = null,
    val place: String,
    val state: String,
    val pincode: String,
    val country: String? = "India"
)
