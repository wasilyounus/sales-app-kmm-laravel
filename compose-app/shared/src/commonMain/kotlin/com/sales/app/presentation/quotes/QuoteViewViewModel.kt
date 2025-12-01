package com.sales.app.presentation.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetPartyByIdUseCase
import com.sales.app.domain.usecase.GetQuoteByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QuoteViewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val quote: QuoteUiModel? = null,
    val items: List<QuoteItemUiModel> = emptyList()
)

class QuoteViewViewModel(
    private val getQuoteByIdUseCase: GetQuoteByIdUseCase,
    private val getPartyByIdUseCase: GetPartyByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteViewUiState())
    val uiState: StateFlow<QuoteViewUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadQuote(accountId: Int, quoteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getQuoteByIdUseCase(quoteId)
                .flatMapLatest { quote ->
                    if (quote != null) {
                        getPartyByIdUseCase(accountId, quote.partyId).map { party ->
                            Pair(quote, party)
                        }
                    } else {
                        flowOf(null)
                    }
                }
                .collect { pair ->
                    if (pair != null) {
                        val (quote, party) = pair
                        val partyName = party?.name ?: "Unknown Party"
                        
                        val items = quote.items.map { item ->
                            QuoteItemUiModel(
                                id = item.id,
                                itemId = item.itemId,
                                itemName = "Item #${item.itemId}", // Ideally fetch item name
                                price = item.price.toString(),
                                qty = item.qty.toString()
                            )
                        }
                        
                        val quoteUiModel = QuoteUiModel(
                            id = quote.id,
                            partyName = partyName,
                            date = quote.date,
                            amount = quote.items.sumOf { it.price * it.qty },
                            itemsCount = quote.items.size
                        )
                        
                        _uiState.update {
                            it.copy(
                                quote = quoteUiModel,
                                items = items,
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Quote not found")
                        }
                    }
                }
        }
    }
}
