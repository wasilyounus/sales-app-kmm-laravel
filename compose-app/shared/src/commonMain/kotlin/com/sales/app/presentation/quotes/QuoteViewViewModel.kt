package com.sales.app.presentation.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetItemsUseCase
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
    private val getPartyByIdUseCase: GetPartyByIdUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteViewUiState())
    val uiState: StateFlow<QuoteViewUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadQuote(accountId: Int, quoteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                getQuoteByIdUseCase(quoteId),
                getItemsUseCase(accountId)
            ) { quote, allItems ->
                if (quote != null) {
                    val party = getPartyByIdUseCase(accountId, quote.partyId).firstOrNull()
                    Triple(quote, party, allItems)
                } else {
                    null
                }
            }.collect { triple ->
                if (triple != null) {
                    val (quote, party, allItems) = triple
                    val partyName = party?.name ?: "Unknown Party"
                    
                    val items = quote.items.map { item ->
                        val itemDetails = allItems.find { it.id == item.itemId }
                        QuoteItemUiModel(
                            id = item.id,
                            itemId = item.itemId,
                            itemName = itemDetails?.name ?: "Item #${item.itemId}",
                            price = item.price.toString(),
                            qty = item.qty.toString()
                        )
                    }
                    
                    val quoteUiModel = QuoteUiModel(
                        id = quote.id,
                        quoteNo = quote.quoteNo,
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
