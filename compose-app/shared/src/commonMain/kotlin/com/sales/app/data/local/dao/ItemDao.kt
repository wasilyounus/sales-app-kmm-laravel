package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE accountId = :accountId AND deletedAt IS NULL ORDER BY name ASC")
    fun getItemsByAccount(accountId: Int): Flow<List<ItemEntity>>
    
    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Int): Flow<ItemEntity?>
    
    @Query("SELECT * FROM items WHERE accountId = :accountId AND name LIKE '%' || :query || '%' AND deletedAt IS NULL")
    fun searchItems(accountId: Int, query: String): Flow<List<ItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)
    
    @Update
    suspend fun updateItem(item: ItemEntity)
    
    @Delete
    suspend fun deleteItem(item: ItemEntity)
    
    @Query("DELETE FROM items WHERE accountId = :accountId")
    suspend fun deleteItemsByAccount(accountId: Int)
}
