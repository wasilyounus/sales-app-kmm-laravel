package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.PurchaseEntity
import com.sales.app.data.local.entity.PurchaseItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases WHERE accountId = :accountId AND deletedAt IS NULL ORDER BY date DESC")
    fun getPurchasesByAccount(accountId: Int): Flow<List<PurchaseEntity>>
    
    @Query("SELECT * FROM purchases WHERE id = :id")
    fun getPurchaseById(id: Int): Flow<PurchaseEntity?>
    
    @Query("SELECT * FROM purchase_items WHERE purchaseId = :purchaseId")
    fun getPurchaseItems(purchaseId: Int): Flow<List<PurchaseItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchases(purchases: List<PurchaseEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseItems(items: List<PurchaseItemEntity>)
    
    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity)
    
    @Delete
    suspend fun deletePurchase(purchase: PurchaseEntity)
    
    @Query("DELETE FROM purchase_items WHERE purchaseId = :purchaseId")
    suspend fun deletePurchaseItems(purchaseId: Int)
    
    @Transaction
    suspend fun insertPurchaseWithItems(purchase: PurchaseEntity, items: List<PurchaseItemEntity>) {
        val purchaseId = insertPurchase(purchase)
        insertPurchaseItems(items)
    }
}
