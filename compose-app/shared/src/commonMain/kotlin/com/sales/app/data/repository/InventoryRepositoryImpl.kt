@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.data.repository

import com.sales.app.data.local.dao.InventoryDao
import com.sales.app.data.local.dao.ItemDao
import com.sales.app.data.local.entity.StockMovementEntity
import com.sales.app.domain.model.InventorySummary
import com.sales.app.domain.model.StockMovement
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import com.sales.app.domain.repository.InventoryRepository

class InventoryRepositoryImpl(
    private val inventoryDao: InventoryDao,
    private val itemDao: ItemDao
) : InventoryRepository {
    override fun getInventorySummary(companyId: Int): Flow<List<InventorySummary>> {
        val itemsFlow = itemDao.getItemsByAccount(companyId)
        val movementsFlow = inventoryDao.getAllStockMovements(companyId)

        return combine(itemsFlow, movementsFlow) { items, movements ->
            items.map { item ->
                val itemMovements = movements.filter { it.itemId == item.id }
                val totalIn = itemMovements.filter { it.type == "IN" }.sumOf { it.qty }
                val totalOut = itemMovements.filter { it.type == "OUT" }.sumOf { it.qty }
                val currentStock = totalIn - totalOut
                
                val lastUpdated = itemMovements.maxByOrNull { it.date }?.date ?: ""

                InventorySummary(
                    itemId = item.id,
                    itemName = item.name,
                    currentStock = currentStock,
                    lastUpdated = lastUpdated
                )
            }
        }
    }

    override fun getStockMovements(itemId: Int): Flow<List<StockMovement>> {
        return inventoryDao.getStockMovements(itemId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun adjustStock(
        itemId: Int,
        qty: Double,
        type: String,
        reason: String,
        companyId: Int
    ): Result<Unit> {
        return try {
            val now = com.sales.app.util.TimeProvider.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            val entity = StockMovementEntity(
                itemId = itemId,
                qty = qty,
                type = type,
                date = now,
                referenceId = null,
                referenceType = "ADJUSTMENT",
                reason = reason,
                companyId = companyId,
                createdAt = now,
                updatedAt = now
            )
            inventoryDao.insertStockMovement(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to adjust stock: ${e.message}", e)
        }
    }

    private fun StockMovementEntity.toDomainModel() = StockMovement(
        id = id,
        itemId = itemId,
        qty = qty,
        type = type,
        date = date,
        referenceId = referenceId,
        referenceType = referenceType,
        reason = reason,
        companyId = companyId
    )
}
