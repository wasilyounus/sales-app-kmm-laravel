package com.sales.app.presentation.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetItemsUseCase
import com.sales.app.domain.usecase.GetPartyByIdUseCase
import com.sales.app.domain.usecase.GetPurchaseByIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PurchaseViewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val purchase: PurchaseUiModel? = null,
    val items: List<PurchaseItemUiModel> = emptyList()
)

class PurchaseViewViewModel(
    private val getPurchaseByIdUseCase: GetPurchaseByIdUseCase,
    private val getPartyByIdUseCase: GetPartyByIdUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseViewUiState())
    val uiState: StateFlow<PurchaseViewUiState> = _uiState.asStateFlow()

    fun loadPurchase(companyId: Int, purchaseId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                getPurchaseByIdUseCase(purchaseId),
                getItemsUseCase(companyId)
            ) { purchase, allItems ->
                if (purchase != null) {
                    val party = getPartyByIdUseCase(companyId, purchase.partyId).firstOrNull()
                    Triple(purchase, party, allItems)
                } else {
                    null
                }
            }.collect { triple ->
                if (triple != null) {
                    val (purchase, party, allItems) = triple
                    val partyName = party?.name ?: "Unknown Party"
                    
                    val items = purchase.items.map { item ->
                        val itemDetails = allItems.find { it.id == item.itemId }
                        PurchaseItemUiModel(
                            itemId = item.itemId,
                            itemName = itemDetails?.name ?: "Item #${item.itemId}",
                            price = item.price.toString(),
                            qty = item.qty.toString(),
                            taxId = item.taxId
                        )
                    }
                    
                    val purchaseUiModel = PurchaseUiModel(
                        id = purchase.id,
                        invoiceNo = purchase.invoiceNo,
                        partyName = partyName,
                        date = purchase.date,
                        amount = purchase.items.sumOf { it.price * it.qty }.toString(),
                        itemsCount = purchase.items.size
                    )
                    
                    _uiState.update {
                        it.copy(
                            purchase = purchaseUiModel,
                            items = items,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Purchase not found")
                    }
                }
            }
        }
    }
}
