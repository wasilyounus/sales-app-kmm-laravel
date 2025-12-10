@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.data.remote.dto.SaleItemRequest
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Party
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.sales.app.util.TimeProvider
import kotlin.time.ExperimentalTime

data class SaleFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val parties: List<Party> = emptyList(),
    val items: List<Item> = emptyList(),
    
    // Form fields
    val partyId: Int? = null,
    val date: String = TimeProvider.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
    val invoiceNo: String = "",
    val selectedItems: List<SaleItemUiModel> = emptyList(),
    val taxId: Int? = null,
    
    // Validation
    val isPartyValid: Boolean = true,
    val isDateValid: Boolean = true,
    val isInvoiceNoValid: Boolean = true,
    val isItemsValid: Boolean = true
)



@OptIn(kotlin.time.ExperimentalTime::class)
class SaleFormViewModel(
    private val createSaleUseCase: CreateSaleUseCase,
    private val getSaleByIdUseCase: GetSaleByIdUseCase,
    private val updateSaleUseCase: UpdateSaleUseCase,
    private val getPartiesUseCase: GetPartiesUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SaleFormUiState())
    val uiState: StateFlow<SaleFormUiState> = _uiState.asStateFlow()
    
    private var currentSaleId: Int? = null
    
    fun loadData(accountId: Int, saleId: Int? = null) {
        currentSaleId = saleId
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
                
                // If editing, load sale data
                if (saleId != null) {
                    loadSale(saleId)
                }
            }
        }
    }
    
    private fun loadSale(saleId: Int) {
        viewModelScope.launch {
            getSaleByIdUseCase(saleId).collect { sale ->
                sale?.let { s ->
                    _uiState.update { state ->
                        state.copy(
                            partyId = s.partyId,
                            date = s.date,
                            invoiceNo = s.invoiceNo,
                            taxId = s.taxId,
                            selectedItems = s.items.map { item ->
                                val itemDetails = state.items.find { it.id == item.itemId }
                                SaleItemUiModel(
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
        _uiState.update { it.copy(invoiceNo = invoiceNo, isInvoiceNoValid = true) }
    }
    
    fun onAddItem(item: Item, qty: Double, price: Double) {
        _uiState.update { state ->
            val newItem = SaleItemUiModel(
                itemId = item.id,
                itemName = item.name,
                price = price.toString(),
                qty = qty.toString(),
                taxId = null // Default tax
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
    
    fun saveSale(accountId: Int, onSuccess: () -> Unit) {
        val state = uiState.value
        
        // Validation
        val isPartyValid = state.partyId != null
        val isDateValid = state.date.isNotBlank()
        val isInvoiceNoValid = state.invoiceNo.isNotBlank()
        val isItemsValid = state.selectedItems.isNotEmpty()
        
        if (!isPartyValid || !isDateValid || !isInvoiceNoValid || !isItemsValid) {
            _uiState.update { 
                it.copy(
                    isPartyValid = isPartyValid,
                    isDateValid = isDateValid,
                    isInvoiceNoValid = isInvoiceNoValid,
                    isItemsValid = isItemsValid,
                    error = "Please fill all required fields"
                )
            }
            return
        }
        
        _uiState.update { it.copy(isSaving = true, error = null) }
        
        viewModelScope.launch {
            val itemsRequest = state.selectedItems.map { 
                SaleItemRequest(
                    item_id = it.itemId,
                    price = it.price.toDoubleOrNull() ?: 0.0,
                    qty = it.qty.toDoubleOrNull() ?: 0.0,
                    tax_id = it.taxId
                )
            }
            
            val result = if (currentSaleId == null) {
                createSaleUseCase(
                    partyId = state.partyId!!,
                    date = state.date,
                    invoiceNo = state.invoiceNo,
                    taxId = state.taxId,
                    items = itemsRequest,
                    accountId = accountId
                )
            } else {
                updateSaleUseCase(
                    id = currentSaleId!!,
                    partyId = state.partyId!!,
                    date = state.date,
                    invoiceNo = state.invoiceNo,
                    taxId = state.taxId,
                    items = itemsRequest,
                    accountId = accountId
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
