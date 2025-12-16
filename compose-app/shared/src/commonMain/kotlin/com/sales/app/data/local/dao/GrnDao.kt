package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.GrnEntity
import com.sales.app.data.local.entity.GrnItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrnDao {
    @Query("SELECT * FROM grns WHERE companyId = :companyId AND deletedAt IS NULL ORDER BY date DESC")
    fun getGrnsByAccount(companyId: Int): Flow<List<GrnEntity>>
    
    @Query("SELECT * FROM grns WHERE purchaseId = :purchaseId AND deletedAt IS NULL ORDER BY date DESC")
    fun getGrnsByPurchase(purchaseId: Int): Flow<List<GrnEntity>>
    
    @Query("SELECT * FROM grns WHERE id = :id")
    fun getGrnById(id: Int): Flow<GrnEntity?>
    
    @Query("SELECT * FROM grn_items WHERE grnId = :grnId")
    fun getGrnItems(grnId: Int): Flow<List<GrnItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrn(grn: GrnEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrns(grns: List<GrnEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrnItems(items: List<GrnItemEntity>)
    
    @Update
    suspend fun updateGrn(grn: GrnEntity)
    
    @Delete
    suspend fun deleteGrn(grn: GrnEntity)
    
    @Query("DELETE FROM grn_items WHERE grnId = :grnId")
    suspend fun deleteGrnItems(grnId: Int)
    
    @Transaction
    suspend fun insertGrnWithItems(grn: GrnEntity, items: List<GrnItemEntity>) {
        insertGrn(grn)
        insertGrnItems(items)
    }
}
