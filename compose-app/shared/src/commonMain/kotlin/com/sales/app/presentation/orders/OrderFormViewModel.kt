@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.data.remote.dto.OrderItemRequest
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Party
import com.sales.app.domain.usecase.*
import com.sales.app.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.sales.app.util.TimeProvider
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class OrderFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val parties: List<Party> = emptyList(),
    val items: List<Item> = emptyList(),
    
    // Form fields
    val partyId: Int? = null,
    val date: String = TimeProvider.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
    val orderNo: String = "",
    val selectedItems: List<OrderItemUiModel> = emptyList(),
    
    // Validation
    val isPartyValid: Boolean = true,
    val isDateValid: Boolean = true,
    val isItemsValid: Boolean = true
)

@OptIn(kotlin.time.ExperimentalTime::class)
class OrderFormViewModel(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val getPartiesUseCase: GetPartiesUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OrderFormUiState())
    val uiState: StateFlow<OrderFormUiState> = _uiState.asStateFlow()
    
    private var currentOrderId: Int? = null
    
    fun loadData(accountId: Int, orderId: Int? = null) {
        currentOrderId = orderId
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
                
                // If editing, load order data
                if (orderId != null) {
                    loadOrder(orderId)
                }
            }
        }
    }
    
    private fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            getOrderByIdUseCase(orderId).collect { order ->
                order?.let { o ->
                    _uiState.update { state ->
                        state.copy(
                            partyId = o.partyId,
                            date = o.date,
                            orderNo = o.orderNo ?: "",
                            selectedItems = o.items.map { item ->
                                val itemDetails = state.items.find { it.id == item.itemId }
                                OrderItemUiModel(
                                    itemId = item.itemId,
                                    itemName = itemDetails?.name ?: "Unknown Item",
                                    price = item.price.toString(),
                                    qty = item.qty.toString()
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

    fun onOrderNoChange(orderNo: String) {
        _uiState.update { it.copy(orderNo = orderNo) }
    }
    
    fun onAddItem(item: Item, qty: Double, price: Double) {
        _uiState.update { state ->
            val newItem = OrderItemUiModel(
                itemId = item.id,
                itemName = item.name,
                price = price.toString(),
                qty = qty.toString()
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
    
    fun saveOrder(accountId: Int, onSuccess: () -> Unit) {
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
                OrderItemRequest(
                    item_id = it.itemId,
                    price = it.price.toDoubleOrNull() ?: 0.0,
                    qty = it.qty.toDoubleOrNull() ?: 0.0
                )
            }
            
            val result = if (currentOrderId == null) {
                createOrderUseCase(
                    partyId = state.partyId!!,
                    date = state.date,
                    items = itemsRequest,
                    accountId = accountId,
                    orderNo = state.orderNo.takeIf { it.isNotBlank() }
                )
            } else {
                updateOrderUseCase(
                    id = currentOrderId!!,
                    partyId = state.partyId!!,
                    date = state.date,
                    items = itemsRequest,
                    accountId = accountId,
                    orderNo = state.orderNo.takeIf { it.isNotBlank() }
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
