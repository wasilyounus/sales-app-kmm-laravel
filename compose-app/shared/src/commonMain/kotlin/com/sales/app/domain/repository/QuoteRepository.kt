package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.QuoteItemRequest
import com.sales.app.domain.model.Quote
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    fun getQuotesByAccount(companyId: Int): Flow<List<Quote>>
    fun getQuoteById(quoteId: Int): Flow<Quote?>
    suspend fun syncQuotes(accountId: Int): Result<Unit>
    suspend fun createQuote(
        partyId: Int,
        date: String,
        items: List<QuoteItemRequest>,
        accountId: Int,
        quoteNo: String? = null
    ): Result<Quote>
    suspend fun updateQuote(
        id: Int,
        partyId: Int,
        date: String,
        items: List<QuoteItemRequest>,
        accountId: Int,
        quoteNo: String? = null
    ): Result<Quote>
    suspend fun deleteQuote(id: Int): Result<Unit>
}
