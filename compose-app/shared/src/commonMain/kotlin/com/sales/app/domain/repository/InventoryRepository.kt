package com.sales.app.domain.repository

import com.sales.app.domain.model.InventorySummary
import com.sales.app.domain.model.StockMovement
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventorySummary(companyId: Int): Flow<List<InventorySummary>>
    fun getStockMovements(itemId: Int): Flow<List<StockMovement>>
    suspend fun adjustStock(
        itemId: Int,
        qty: Double,
        type: String,
        reason: String,
        companyId: Int
    ): Result<Unit>
}
