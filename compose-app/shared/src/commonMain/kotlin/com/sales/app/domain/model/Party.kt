package com.sales.app.domain.model

data class Party(
    val id: Int,
    val name: String,
    val gst: String?,
    val phone: String?,
    val email: String?,
    val accountId: Int,
    val addresses: List<Address> = emptyList()
)


