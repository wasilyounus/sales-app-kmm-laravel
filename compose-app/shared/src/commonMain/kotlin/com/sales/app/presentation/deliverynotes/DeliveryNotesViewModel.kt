package com.sales.app.presentation.deliverynotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.DeliveryNote
import com.sales.app.domain.usecase.GetDeliveryNotesUseCase
import com.sales.app.domain.usecase.SyncDeliveryNotesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeliveryNotesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val deliveryNotes: List<DeliveryNote> = emptyList(),
    val error: String? = null
)

class DeliveryNotesViewModel(
    private val getDeliveryNotesUseCase: GetDeliveryNotesUseCase,
    private val syncDeliveryNotesUseCase: SyncDeliveryNotesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeliveryNotesUiState())
    val uiState: StateFlow<DeliveryNotesUiState> = _uiState.asStateFlow()
    
    fun loadDeliveryNotes(accountId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Sync in background
            launch {
                try {
                    syncDeliveryNotesUseCase(accountId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            getDeliveryNotesUseCase(accountId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(isLoading = false, isRefreshing = false, error = e.message)
                    }
                }
                .collect { notes ->
                    _uiState.update { 
                        it.copy(
                            deliveryNotes = notes,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
    
    fun onRefresh(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadDeliveryNotes(accountId)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
