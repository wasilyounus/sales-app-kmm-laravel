package com.sales.app.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetItemsUseCase
import com.sales.app.domain.usecase.GetPartyByIdUseCase
import com.sales.app.domain.usecase.GetSaleByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SaleViewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sale: SaleUiModel? = null,
    val items: List<SaleItemUiModel> = emptyList()
)

class SaleViewViewModel(
    private val getSaleByIdUseCase: GetSaleByIdUseCase,
    private val getPartyByIdUseCase: GetPartyByIdUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleViewUiState())
    val uiState: StateFlow<SaleViewUiState> = _uiState.asStateFlow()

    fun loadSale(companyId: Int, saleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                getSaleByIdUseCase(saleId),
                getItemsUseCase(companyId)
            ) { sale, allItems ->
                if (sale != null) {
                    val party = getPartyByIdUseCase(companyId, sale.partyId).firstOrNull()
                    Triple(sale, party, allItems)
                } else {
                    null
                }
            }.collect { triple ->
                if (triple != null) {
                    val (sale, party, allItems) = triple
                    val partyName = party?.name ?: "Unknown Party"
                    
                    val items = sale.items.map { item ->
                        val itemDetails = allItems.find { it.id == item.itemId }
                        SaleItemUiModel(
                            itemId = item.itemId,
                            itemName = itemDetails?.name ?: "Item #${item.itemId}",
                            price = item.price.toString(),
                            qty = item.qty.toString(),
                            taxId = item.taxId
                        )
                    }
                    
                    val saleUiModel = SaleUiModel(
                        id = sale.id,
                        partyName = partyName,
                        date = sale.date,
                        invoiceNo = sale.invoiceNo,
                        itemsCount = sale.items.size,
                        amount = sale.items.sumOf { it.price * it.qty }.toString()
                    )
                    
                    _uiState.update {
                        it.copy(
                            sale = saleUiModel,
                            items = items,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Sale not found")
                    }
                }
            }
        }
    }
}
