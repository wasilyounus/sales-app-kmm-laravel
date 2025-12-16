package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.SaleEntity
import com.sales.app.data.local.entity.SaleItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales WHERE companyId = :companyId AND deletedAt IS NULL ORDER BY date DESC")
    fun getSalesByAccount(companyId: Int): Flow<List<SaleEntity>>
    
    @Query("SELECT * FROM sales WHERE id = :id")
    fun getSaleById(id: Int): Flow<SaleEntity?>
    
    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getSaleItems(saleId: Int): Flow<List<SaleItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<SaleEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItemEntity>)
    
    @Update
    suspend fun updateSale(sale: SaleEntity)
    
    @Delete
    suspend fun deleteSale(sale: SaleEntity)
    
    @Query("DELETE FROM sale_items WHERE saleId = :saleId")
    suspend fun deleteSaleItems(saleId: Int)
    
    @Transaction
    suspend fun insertSaleWithItems(sale: SaleEntity, items: List<SaleItemEntity>) {
        val saleId = insertSale(sale)
        insertSaleItems(items)
    }
}
