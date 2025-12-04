package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.PurchaseItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseItemDao {
    @Query("SELECT * FROM purchase_items WHERE purchaseId = :purchaseId AND deletedAt IS NULL")
    fun getPurchaseItemsByPurchaseId(purchaseId: Int): Flow<List<PurchaseItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseItem(purchaseItem: PurchaseItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseItems(purchaseItems: List<PurchaseItemEntity>)
    
    @Update
    suspend fun updatePurchaseItem(purchaseItem: PurchaseItemEntity)
    
    @Delete
    suspend fun deletePurchaseItem(purchaseItem: PurchaseItemEntity)
    
    @Query("DELETE FROM purchase_items WHERE purchaseId = :purchaseId")
    suspend fun deletePurchaseItemsByPurchaseId(purchaseId: Int)
    
    @Query("DELETE FROM purchase_items WHERE accountId = :accountId")
    suspend fun deletePurchaseItemsByAccount(accountId: Int)
}
