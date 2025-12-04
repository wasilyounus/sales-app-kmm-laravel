package com.sales.app.domain.model

data class Item(
    val id: Int,
    val name: String,
    val altName: String?,
    val brand: String?,
    val size: String?,
    val uqc: Int,
    val hsn: Int?,
    val accountId: Int,
    val taxId: Int? = null
)

data class Uqc(
    val id: Int,
    val uqc: String,
    val quantity: String?,
    val type: String?,
    val active: Boolean
)

data class Tax(
    val id: Int,
    val schemeName: String,
    val country: String? = null,
    val tax1Name: String?,
    val tax1Val: Double?,
    val tax2Name: String?,
    val tax2Val: Double?,
    val tax3Name: String?,
    val tax3Val: Double?,
    val tax4Name: String?,
    val tax4Val: Double?,
    val active: Boolean
)

