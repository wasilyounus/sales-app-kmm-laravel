package com.sales.app.presentation.parties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Party
import com.sales.app.domain.usecase.GetPartiesUseCase
import com.sales.app.domain.usecase.SearchPartiesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

data class PartiesUiState(
    val isLoading: Boolean = false,
    val parties: List<Party> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class PartiesViewModel(
    private val getPartiesUseCase: GetPartiesUseCase,
    private val searchPartiesUseCase: SearchPartiesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PartiesUiState())
    val uiState: StateFlow<PartiesUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    
    fun loadParties(accountId: Int) {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    _uiState.update { it.copy(isLoading = true, searchQuery = query) }
                    searchPartiesUseCase(accountId, query)
                }
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = e.message ?: "Unknown error"
                        ) 
                    }
                }
                .collect { parties ->
                    _uiState.update { 
                        it.copy(
                            parties = parties, 
                            isLoading = false, 
                            error = null
                        ) 
                    }
                }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
