package com.sales.app.presentation.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.sales.app.domain.model.Company
import com.sales.app.domain.usecase.FetchCompaniesUseCase
import com.sales.app.domain.usecase.GetCompaniesUseCase
import com.sales.app.domain.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val fetchCompaniesUseCase: FetchCompaniesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    // -1 indicates no selection or loading
    private val _selectedCompanyId = MutableStateFlow<Int>(-1)
    val selectedCompanyId: StateFlow<Int> = _selectedCompanyId.asStateFlow()
    
    // Blocking dialog state
    private val _showCompanySelectionDialog = MutableStateFlow<Boolean>(false)
    val showCompanySelectionDialog: StateFlow<Boolean> = _showCompanySelectionDialog.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Loading state for selection
    private val _isSelectingCompany = MutableStateFlow(false)
    val isSelectingCompany: StateFlow<Boolean> = _isSelectingCompany.asStateFlow()

    val companies: StateFlow<List<Company>> = getCompaniesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentUser = authRepository.currentUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        loadCompanies()
        observeUser()
    }
    
    private fun observeUser() {
        viewModelScope.launch {
            println("MainViewModel: observeUser started")
            authRepository.currentUser().collectLatest { user ->
                println("MainViewModel: User emitted: ${user?.id}, company: ${user?.currentCompanyId}")
                if (user != null) {
                    if (user.currentCompanyId != null) {
                        _selectedCompanyId.value = user.currentCompanyId
                        _showCompanySelectionDialog.value = false
                        loadCompanies() // Ensure companies are loaded even if selected
                    } else {
                        // User logged in but no company selected -> Block action
                        _showCompanySelectionDialog.value = true
                        loadCompanies() // Ensure companies are loaded
                    }
                } else {
                    // Not logged in
                    _showCompanySelectionDialog.value = false
                }
            }
        }
    }

    fun loadCompanies() {
        println("MainViewModel: loadCompanies called")
        viewModelScope.launch {
            try {
                println("MainViewModel: fetchCompaniesUseCase invoking")
                fetchCompaniesUseCase()
                println("MainViewModel: fetchCompaniesUseCase finished")
                // Check if companies are empty after fetch
                // We observe the flow 'companies', so checking verified data might require a slight delay or observing the result of fetch
                 // Ideally fetch returns the list or we check the state flow
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to load companies: ${e.message}"
            }
        }
    }

    fun selectCompany(id: Int) {
        viewModelScope.launch {
            _isSelectingCompany.value = true
            _errorMessage.value = null
            
            val result = authRepository.selectCompany(id)
            
            when (result) {
                is com.sales.app.util.Result.Success -> {
                    _selectedCompanyId.value = id
                    _showCompanySelectionDialog.value = false
                    // DB update is handled by repository and observeUser
                }
                is com.sales.app.util.Result.Error -> {
                    _errorMessage.value = result.message
                }
                is com.sales.app.util.Result.Loading -> {}
            }
            _isSelectingCompany.value = false
        }
    }
}
