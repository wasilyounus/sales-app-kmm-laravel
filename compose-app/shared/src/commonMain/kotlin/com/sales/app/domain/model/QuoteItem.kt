package com.sales.app.domain.model

data class QuoteItem(
    val id: Int,
    val quoteId: Int,
    val itemId: Int,
    val price: Double,
    val qty: Double,
    val accountId: Int
)
