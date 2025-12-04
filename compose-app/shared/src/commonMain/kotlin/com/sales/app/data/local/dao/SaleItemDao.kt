package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.SaleItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleItemDao {
    @Query("SELECT * FROM sale_items WHERE saleId = :saleId AND deletedAt IS NULL")
    fun getSaleItemsBySaleId(saleId: Int): Flow<List<SaleItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItem(saleItem: SaleItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(saleItems: List<SaleItemEntity>)
    
    @Update
    suspend fun updateSaleItem(saleItem: SaleItemEntity)
    
    @Delete
    suspend fun deleteSaleItem(saleItem: SaleItemEntity)
    
    @Query("DELETE FROM sale_items WHERE saleId = :saleId")
    suspend fun deleteSaleItemsBySaleId(saleId: Int)
    
    @Query("DELETE FROM sale_items WHERE accountId = :accountId")
    suspend fun deleteSaleItemsByAccount(accountId: Int)
}
