package com.sales.app.domain.model

data class StockMovement(
    val id: Int,
    val itemId: Int,
    val qty: Double,
    val type: String, // "IN" or "OUT"
    val date: String,
    val referenceId: Int?, // Sale ID, Purchase ID, etc.
    val referenceType: String?, // "SALE", "PURCHASE", "ADJUSTMENT"
    val reason: String?,
    val accountId: Int
)
