package com.sales.app.domain.model

data class Quote(
    val id: Int,
    val partyId: Int,
    val date: String,
    val quoteNo: String? = null,
    val companyId: Int,
    val items: List<QuoteItem> = emptyList()
)
