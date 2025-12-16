package com.sales.app.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Item
import com.sales.app.domain.usecase.AdjustStockUseCase
import com.sales.app.domain.usecase.GetItemsUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockAdjustmentUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val items: List<Item> = emptyList(),
    
    // Form fields
    val itemId: Int? = null,
    val type: String = "IN", // "IN" or "OUT"
    val qty: String = "",
    val reason: String = "",
    
    val isItemValid: Boolean = true,
    val isQtyValid: Boolean = true
)

class StockAdjustmentViewModel(
    private val adjustStockUseCase: AdjustStockUseCase,
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockAdjustmentUiState())
    val uiState: StateFlow<StockAdjustmentUiState> = _uiState.asStateFlow()

    fun loadItems(companyId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getItemsUseCase(companyId).collect { items ->
                _uiState.update { 
                    it.copy(
                        items = items,
                        isLoading = false
                    )
                }
            }
        }
    }


    fun onItemChange(itemId: Int) {
        _uiState.update { it.copy(itemId = itemId, isItemValid = true) }
    }

    fun onTypeChange(type: String) {
        _uiState.update { it.copy(type = type) }
    }

    fun onQtyChange(qty: String) {
        _uiState.update { it.copy(qty = qty, isQtyValid = true) }
    }

    fun onReasonChange(reason: String) {
        _uiState.update { it.copy(reason = reason) }
    }

    fun saveAdjustment(companyId: Int, onSuccess: () -> Unit) {
        val state = uiState.value
        
        val isItemValid = state.itemId != null
        val isQtyValid = state.qty.toDoubleOrNull() != null && state.qty.toDouble() > 0
        
        if (!isItemValid || !isQtyValid) {
            _uiState.update { 
                it.copy(
                    isItemValid = isItemValid,
                    isQtyValid = isQtyValid,
                    error = "Please fill all required fields correctly"
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val result = adjustStockUseCase(
                itemId = state.itemId!!,
                qty = state.qty.toDouble(),
                type = state.type,
                reason = state.reason,
                companyId = companyId
            )

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
                    _uiState.update { it.copy(isSaving = true) }
                }
            }
        }
    }
}
