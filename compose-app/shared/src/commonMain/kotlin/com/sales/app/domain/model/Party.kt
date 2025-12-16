package com.sales.app.domain.model

data class Party(
    val id: Int,
    val name: String,
    val taxNumber: String?,
    val phone: String?,
    val email: String?,
    val companyId: Int,
    val addresses: List<Address> = emptyList()
)


