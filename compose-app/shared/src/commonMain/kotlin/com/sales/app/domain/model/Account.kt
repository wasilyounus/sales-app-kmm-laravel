package com.sales.app.domain.model

data class Account(
    val id: Int,
    val name: String,
    val nameFormatted: String,
    val desc: String?,
    val taxationType: Int,
    val taxRate: Int,

    val address: String?,
    val call: String?,
    val whatsapp: String?,
    val footerContent: String?,
    val signature: String?,
    val financialYearStart: String?,
    val country: String? = "India",
    val state: String? = null,
    val taxNumber: String? = null,
    val defaultTaxId: Int? = null,
    val taxApplicationLevel: String = "item",
    val enableDeliveryNotes: Boolean = true,
    val enableGrns: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)
