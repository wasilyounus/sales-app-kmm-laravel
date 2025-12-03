package com.sales.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.data.local.AccountPreferences
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.AccountSelectionDto
import com.sales.app.domain.model.User
import com.sales.app.domain.usecase.LoginUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val showAccountSelection: Boolean = false,
    val accounts: List<AccountSelectionDto> = emptyList(),
    val noAccountsError: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val accountPreferences: AccountPreferences,
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    // Note: Backend should return accounts list
                    // For now, we'll trigger account fetching
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = result.data,
                            error = null
                        )
                    }
                    // Fetch accounts after successful login
                    fetchAccounts()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                            isSuccess = false
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                // Call API to fetch user's accounts
                val response = apiService.getUserAccounts()
                val accounts = response.accounts
                
                if (accounts.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            noAccountsError = true,
                            error = "No accounts assigned. Contact your administrator."
                        )
                    }
                } else if (accounts.size == 1) {
                    // Auto-select single account
                    selectAccount(accounts[0].id)
                } else {
                    _uiState.update {
                        it.copy(
                            showAccountSelection = true,
                            accounts = accounts
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to load accounts: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun selectAccount(accountId: Int) {
        viewModelScope.launch {
            try {
                // Call API to set account
                apiService.selectAccount(accountId)
                
                // Save to DataStore
                accountPreferences.saveCurrentAccount(accountId)
                
                _uiState.update {
                    it.copy(
                        showAccountSelection = false,
                        isSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to select account: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun dismissAccountSelection() {
        _uiState.update { it.copy(showAccountSelection = false) }
    }
}
