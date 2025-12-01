package com.sales.app.domain.model

data class Account(
    val id: Int,
    val name: String,
    val nameFormatted: String,
    val desc: String?,
    val taxationType: Int,
    val taxRate: Int,
    val gst: String?,
    val address: String?,
    val call: String?,
    val whatsapp: String?,
    val footerContent: String?,
    val signature: String?,
    val financialYearStart: String?
)
