package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDto(
    val id: Int = 0,
    val name: String,
    val phone: String?,
    val email: String?,
    val designation: String?,
    @SerialName("is_primary") val isPrimary: Boolean
)
