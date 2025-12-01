package com.sales.app.domain.model

data class Item(
    val id: Int,
    val name: String,
    val altName: String?,
    val brand: String?,
    val size: String?,
    val uqc: Int,
    val hsn: Int?,
    val accountId: Int
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
    val name: String,
    val tax1Name: String?,
    val tax1Rate: Double?,
    val tax2Name: String?,
    val tax2Rate: Double?,
    val tax3Name: String?,
    val tax3Rate: Double?,
    val tax4Name: String?,
    val tax4Rate: Double?,
    val active: Boolean
)
