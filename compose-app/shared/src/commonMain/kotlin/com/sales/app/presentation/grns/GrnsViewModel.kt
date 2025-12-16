package com.sales.app.presentation.grns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Grn
import com.sales.app.domain.usecase.GetGrnsUseCase
import com.sales.app.domain.usecase.SyncGrnsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GrnsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val grns: List<Grn> = emptyList(),
    val error: String? = null
)

class GrnsViewModel(
    private val getGrnsUseCase: GetGrnsUseCase,
    private val syncGrnsUseCase: SyncGrnsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GrnsUiState())
    val uiState: StateFlow<GrnsUiState> = _uiState.asStateFlow()
    
    fun loadGrns(companyId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Sync in background
            launch {
                try {
                    syncGrnsUseCase(companyId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            getGrnsUseCase(companyId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(isLoading = false, isRefreshing = false, error = e.message)
                    }
                }
                .collect { grns ->
                    _uiState.update { 
                        it.copy(
                            grns = grns,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
    
    fun onRefresh(companyId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadGrns(companyId)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
