package com.sales.app.domain.model

data class Sale(
    val id: Int,
    val partyId: Int,
    val date: String,
    val invoiceNo: String,
    val taxId: Int?,
    val companyId: Int,
    val items: List<SaleItem> = emptyList()
)
