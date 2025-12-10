package com.sales.app.domain.usecase

import com.sales.app.domain.repository.InventoryRepository
import com.sales.app.domain.model.InventorySummary
import com.sales.app.domain.model.StockMovement
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetInventorySummaryUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(companyId: Int): Flow<List<InventorySummary>> {
        return repository.getInventorySummary(companyId)
    }
}

class GetStockMovementsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(itemId: Int): Flow<List<StockMovement>> {
        return repository.getStockMovements(itemId)
    }
}

class AdjustStockUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(
        itemId: Int,
        qty: Double,
        type: String,
        reason: String,
        companyId: Int
    ): Result<Unit> {
        return repository.adjustStock(itemId, qty, type, reason, companyId)
    }
}
