package com.sales.app.presentation.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Quote
import com.sales.app.domain.usecase.GetPartiesUseCase
import com.sales.app.domain.usecase.GetQuotesUseCase
import com.sales.app.domain.usecase.SyncQuotesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QuoteUiModel(
    val id: Int,
    val quoteNo: String?,
    val partyName: String,
    val date: String,
    val amount: Double,
    val itemsCount: Int
)

data class QuotesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val quotes: List<QuoteUiModel> = emptyList(),
    val error: String? = null
)

class QuotesViewModel(
    private val getQuotesUseCase: GetQuotesUseCase,
    private val getPartiesUseCase: GetPartiesUseCase,
    private val syncQuotesUseCase: SyncQuotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuotesUiState())
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    fun loadQuotes(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Trigger sync
            launch {
                try {
                    syncQuotesUseCase(accountId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Combine quotes and parties to create UI model
            combine(
                getQuotesUseCase(accountId),
                getPartiesUseCase(accountId)
            ) { quotes, parties ->
                val partyMap = parties.associate { it.id to it.name }
                quotes.map { quote ->
                    QuoteUiModel(
                        id = quote.id,
                        quoteNo = quote.quoteNo,
                        partyName = partyMap[quote.partyId] ?: "Unknown Party",
                        date = quote.date,
                        amount = quote.items.sumOf { it.price * it.qty },
                        itemsCount = quote.items.size
                    )
                }
            }
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
            .collect { quotes ->
                _uiState.update {
                    it.copy(
                        quotes = quotes,
                        isLoading = false,
                        isRefreshing = false,
                        error = null
                    )
                }
            }
        }
    }

    fun onRefresh(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadQuotes(accountId)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
