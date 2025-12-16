package com.sales.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.data.local.CompanyPreferences
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.CompanySelectionDto
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
    val showCompanySelection: Boolean = false,
    val accounts: List<CompanySelectionDto> = emptyList(),
    val noAccountsError: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val companyPreferences: CompanyPreferences,
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    // Note: Backend should return accounts list
                    // For now, we'll trigger account fetching
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = result.data,
                            error = null,
                            isSuccess = true
                        )
                    }
                    // Fetch accounts logic moved to MainViewModel/CompanySwitcher
                    // fetchAccounts()
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
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
