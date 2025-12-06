package com.sales.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sales.app.data.local.entity.PriceListEntity
import com.sales.app.data.local.entity.PriceListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceListDao {
    @Query("SELECT * FROM price_lists WHERE accountId = :accountId ORDER BY name ASC")
    fun getPriceLists(accountId: Int): Flow<List<PriceListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceLists(priceLists: List<PriceListEntity>)

    @Query("SELECT * FROM price_lists WHERE id = :id")
    suspend fun getPriceList(id: Long): PriceListEntity?

    @Query("SELECT * FROM price_list_items WHERE priceListId = :priceListId")
    suspend fun getPriceListItems(priceListId: Long): List<PriceListItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceListItems(items: List<PriceListItemEntity>)

    @Query("DELETE FROM price_list_items WHERE priceListId = :priceListId")
    suspend fun deletePriceListItems(priceListId: Long)
    
    @Query("DELETE FROM price_lists WHERE id = :id")
    suspend fun deletePriceList(id: Long)
}
