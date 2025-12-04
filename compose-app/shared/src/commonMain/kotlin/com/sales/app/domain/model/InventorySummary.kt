package com.sales.app.domain.model

data class InventorySummary(
    val itemId: Int,
    val itemName: String,
    val currentStock: Double,
    val lastUpdated: String
)
