package com.sales.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.usecase.GetItemsUseCase
import com.sales.app.domain.usecase.GetPartiesUseCase
import com.sales.app.domain.usecase.GetQuotesUseCase
import com.sales.app.domain.usecase.GetCompanyUseCase
import com.sales.app.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeStats(
    val itemsCount: Int = 0,
    val partiesCount: Int = 0,
    val quotesCount: Int = 0
)

class HomeViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getItemsUseCase: GetItemsUseCase,
    private val getPartiesUseCase: GetPartiesUseCase,
    private val getQuotesUseCase: GetQuotesUseCase,
    private val getAccountUseCase: GetCompanyUseCase
) : ViewModel() {
    
    private val _stats = MutableStateFlow(HomeStats())
    val stats: StateFlow<HomeStats> = _stats.asStateFlow()
    
    // We need to expose account settings to the UI
    private val _account = MutableStateFlow<com.sales.app.domain.model.Account?>(null)
    val account: StateFlow<com.sales.app.domain.model.Account?> = _account.asStateFlow()
    
    fun loadStats(accountId: Int) {
        viewModelScope.launch {
            // Load Account for settings
            launch {
                getAccountUseCase(accountId).collect {
                    _account.value = it
                }
            }

            combine(
                getItemsUseCase(accountId),
                getPartiesUseCase(accountId),
                getQuotesUseCase(accountId)
            ) { items, parties, quotes ->
                HomeStats(
                    itemsCount = items.size,
                    partiesCount = parties.size,
                    quotesCount = quotes.size
                )
            }.collect { homeStats ->
                _stats.update { homeStats }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
