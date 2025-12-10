package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.QuoteItemRequest
import com.sales.app.domain.repository.QuoteRepository
import com.sales.app.domain.model.Quote
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetQuotesUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(companyId: Int): Flow<List<Quote>> {
        return quoteRepository.getQuotesByAccount(companyId)
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
        companyId: Int,
        quoteNo: String? = null
    ): Result<Quote> {
        return quoteRepository.createQuote(partyId, date, items, companyId, quoteNo)
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
        companyId: Int,
        quoteNo: String? = null
    ): Result<Quote> {
        return quoteRepository.updateQuote(id, partyId, date, items, companyId, quoteNo)
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
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return quoteRepository.syncQuotes(companyId)
    }
}
