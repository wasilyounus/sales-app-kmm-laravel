package com.sales.app.domain.model

data class Address(
    val id: Int,
    val partyId: Int,
    val accountId: Int,
    val line1: String,
    val line2: String?,
    val city: String,
    val district: String?,
    val state: String,
    val pincode: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?
)
