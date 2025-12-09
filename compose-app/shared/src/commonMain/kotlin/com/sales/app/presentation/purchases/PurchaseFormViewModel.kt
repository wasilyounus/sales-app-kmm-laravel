package com.sales.app.presentation.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.data.remote.dto.PurchaseItemRequest
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Party
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PurchaseFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val parties: List<Party> = emptyList(),
    val items: List<Item> = emptyList(),
    
    // Form fields
    val partyId: Int? = null,
    val date: String = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
    val invoiceNo: String = "",
    val selectedItems: List<PurchaseItemUiModel> = emptyList(),
    
    // Validation
    val isPartyValid: Boolean = true,
    val isDateValid: Boolean = true,
    val isItemsValid: Boolean = true
)

class PurchaseFormViewModel(
    private val createPurchaseUseCase: CreatePurchaseUseCase,
    private val getPurchaseByIdUseCase: GetPurchaseByIdUseCase,
    private val updatePurchaseUseCase: UpdatePurchaseUseCase,
    private val getPartiesUseCase: GetPartiesUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PurchaseFormUiState())
    val uiState: StateFlow<PurchaseFormUiState> = _uiState.asStateFlow()
    
    private var currentPurchaseId: Int? = null
    
    fun loadData(accountId: Int, purchaseId: Int? = null) {
        currentPurchaseId = purchaseId
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            // Load parties and items
            combine(
                getPartiesUseCase(accountId),
                getItemsUseCase(accountId)
            ) { parties, items ->
                Pair(parties, items)
            }.collect { (parties, items) ->
                _uiState.update { 
                    it.copy(
                        parties = parties,
                        items = items,
                        isLoading = false
                    )
                }
                
                // If editing, load purchase data
                if (purchaseId != null) {
                    loadPurchase(purchaseId)
                }
            }
        }
    }
    
    private fun loadPurchase(purchaseId: Int) {
        viewModelScope.launch {
            getPurchaseByIdUseCase(purchaseId).collect { purchase ->
                purchase?.let { p ->
                    _uiState.update { state ->
                        state.copy(
                            partyId = p.partyId,
                            date = p.date,
                            invoiceNo = p.invoiceNo ?: "",
                            selectedItems = p.items.map { item ->
                                val itemDetails = state.items.find { it.id == item.itemId }
                                PurchaseItemUiModel(
                                    itemId = item.itemId,
                                    itemName = itemDetails?.name ?: "Unknown Item",
                                    price = item.price.toString(),
                                    qty = item.qty.toString(),
                                    taxId = item.taxId
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    
    fun onPartyChange(partyId: Int) {
        _uiState.update { it.copy(partyId = partyId, isPartyValid = true) }
    }
    
    fun onDateChange(date: String) {
        _uiState.update { it.copy(date = date, isDateValid = true) }
    }

    fun onInvoiceNoChange(invoiceNo: String) {
        _uiState.update { it.copy(invoiceNo = invoiceNo) }
    }
    
    fun onAddItem(item: Item, qty: Double, price: Double) {
        _uiState.update { state ->
            val newItem = PurchaseItemUiModel(
                itemId = item.id,
                itemName = item.name,
                price = price.toString(),
                qty = qty.toString(),
                taxId = null
            )
            state.copy(
                selectedItems = state.selectedItems + newItem,
                isItemsValid = true
            )
        }
    }
    
    fun onRemoveItem(index: Int) {
        _uiState.update { state ->
            val newItems = state.selectedItems.toMutableList().apply { removeAt(index) }
            state.copy(selectedItems = newItems)
        }
    }
    
    fun onUpdateItem(index: Int, qty: Double, price: Double) {
        _uiState.update { state ->
            val newItems = state.selectedItems.toMutableList()
            val item = newItems[index]
            newItems[index] = item.copy(qty = qty.toString(), price = price.toString())
            state.copy(selectedItems = newItems)
        }
    }
    
    fun savePurchase(accountId: Int, onSuccess: () -> Unit) {
        val state = uiState.value
        
        // Validation
        val isPartyValid = state.partyId != null
        val isDateValid = state.date.isNotBlank()
        val isItemsValid = state.selectedItems.isNotEmpty()
        
        if (!isPartyValid || !isDateValid || !isItemsValid) {
            _uiState.update { 
                it.copy(
                    isPartyValid = isPartyValid,
                    isDateValid = isDateValid,
                    isItemsValid = isItemsValid,
                    error = "Please fill all required fields"
                )
            }
            return
        }
        
        _uiState.update { it.copy(isSaving = true, error = null) }
        
        viewModelScope.launch {
            val itemsRequest = state.selectedItems.map { 
                PurchaseItemRequest(
                    item_id = it.itemId,
                    price = it.price.toDoubleOrNull() ?: 0.0,
                    qty = it.qty.toDoubleOrNull() ?: 0.0,
                    tax_id = it.taxId
                )
            }
            
            val result = if (currentPurchaseId == null) {
                createPurchaseUseCase(
                    partyId = state.partyId!!,
                    date = state.date,
                    items = itemsRequest,
                    accountId = accountId,
                    invoiceNo = state.invoiceNo.takeIf { it.isNotBlank() }
                )
            } else {
                updatePurchaseUseCase(
                    id = currentPurchaseId!!,
                    partyId = state.partyId!!,
                    date = state.date,
                    items = itemsRequest,
                    accountId = accountId,
                    invoiceNo = state.invoiceNo.takeIf { it.isNotBlank() }
                )
            }
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Loading state is already handled by isSaving flag
                }
            }
        }
    }
}
