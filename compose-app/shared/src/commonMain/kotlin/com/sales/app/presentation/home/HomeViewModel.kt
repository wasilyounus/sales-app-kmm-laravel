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
    private val getCompanyUseCase: GetCompanyUseCase,
    private val moduleRepository: com.sales.app.domain.repository.ModuleRepository
) : ViewModel() {
    
    private val _stats = MutableStateFlow(HomeStats())
    val stats: StateFlow<HomeStats> = _stats.asStateFlow()
    
    // We need to expose company settings to the UI
    private val _company = MutableStateFlow<com.sales.app.domain.model.Company?>(null)
    val company: StateFlow<com.sales.app.domain.model.Company?> = _company.asStateFlow()
    
    private val _modules = MutableStateFlow<List<com.sales.app.domain.model.Module>>(emptyList())
    val modules: StateFlow<List<com.sales.app.domain.model.Module>> = _modules.asStateFlow()
    
    fun loadStats(companyId: Int) {
        viewModelScope.launch {
            // Fetch Modules
            try {
                _modules.value = moduleRepository.getModules()
            } catch (e: Exception) {
                // Handle error or use defaults (not ideal, but keeps app running)
                println("Failed to fetch modules: ${e.message}")
            }
        
            // Load Company for settings
            launch {
                getCompanyUseCase(companyId).collect {
                    _company.value = it
                }
            }

            combine(
                getItemsUseCase(companyId),
                getPartiesUseCase(companyId),
                getQuotesUseCase(companyId)
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
