package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.QuoteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteItemDao {
    @Query("SELECT * FROM quote_items WHERE quoteId = :quoteId AND deletedAt IS NULL")
    fun getQuoteItemsByQuoteId(quoteId: Int): Flow<List<QuoteItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteItem(quoteItem: QuoteItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteItems(quoteItems: List<QuoteItemEntity>)
    
    @Update
    suspend fun updateQuoteItem(quoteItem: QuoteItemEntity)
    
    @Delete
    suspend fun deleteQuoteItem(quoteItem: QuoteItemEntity)
    
    @Query("DELETE FROM quote_items WHERE quoteId = :quoteId")
    suspend fun deleteQuoteItemsByQuoteId(quoteId: Int)
    
    @Query("DELETE FROM quote_items WHERE companyId = :companyId")
    suspend fun deleteQuoteItemsByAccount(companyId: Int)
}
