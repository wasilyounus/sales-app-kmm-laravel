package com.sales.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sales.app.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(entity: StockMovementEntity)

    @Query("SELECT * FROM stock_movements WHERE itemId = :itemId ORDER BY date DESC")
    fun getStockMovements(itemId: Int): Flow<List<StockMovementEntity>>

    @Query("SELECT * FROM stock_movements WHERE accountId = :accountId")
    fun getAllStockMovements(accountId: Int): Flow<List<StockMovementEntity>>
}
