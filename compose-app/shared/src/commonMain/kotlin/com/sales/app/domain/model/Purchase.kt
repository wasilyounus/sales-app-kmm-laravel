package com.sales.app.domain.model

data class Purchase(
    val id: Int,
    val partyId: Int,
    val date: String,
    val invoiceNo: String? = null,
    val companyId: Int,
    val items: List<PurchaseItem> = emptyList()
)
