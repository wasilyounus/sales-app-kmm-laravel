package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.QuoteItemRequest
import com.sales.app.data.repository.QuoteRepository
import com.sales.app.domain.model.Quote
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetQuotesUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(accountId: Int): Flow<List<Quote>> {
        return quoteRepository.getQuotesByAccount(accountId)
    }
}

class GetQuoteByIdUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(quoteId: Int): Flow<Quote?> {
        return quoteRepository.getQuoteById(quoteId)
    }
}

class CreateQuoteUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(
        partyId: Int,
        date: String,
        items: List<QuoteItemRequest>,
        accountId: Int
    ): Result<Quote> {
        return quoteRepository.createQuote(partyId, date, items, accountId)
    }
}

class UpdateQuoteUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(
        id: Int,
        partyId: Int,
        date: String,
        items: List<QuoteItemRequest>,
        accountId: Int
    ): Result<Quote> {
        return quoteRepository.updateQuote(id, partyId, date, items, accountId)
    }
}

class DeleteQuoteUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return quoteRepository.deleteQuote(id)
    }
}

class SyncQuotesUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Unit> {
        return quoteRepository.syncQuotes(accountId)
    }
}
