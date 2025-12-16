package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE companyId = :companyId AND deletedAt IS NULL ORDER BY date DESC")
    fun getQuotesByAccount(companyId: Int): Flow<List<QuoteEntity>>
    
    @Query("SELECT * FROM quotes WHERE id = :id")
    fun getQuoteById(id: Int): Flow<QuoteEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)
    
    @Update
    suspend fun updateQuote(quote: QuoteEntity)
    
    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)
    
    @Query("DELETE FROM quotes WHERE companyId = :companyId")
    suspend fun deleteQuotesByAccount(companyId: Int)
}
