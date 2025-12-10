package com.sales.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Company
import com.sales.app.domain.usecase.FetchCompanyUseCase
import com.sales.app.domain.usecase.GetCompanyUseCase
import com.sales.app.domain.usecase.UpdateCompanyUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CompanySettingsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val account: Account? = null,
    val taxes: List<com.sales.app.domain.model.Tax> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class CompanySettingsViewModel(
    private val getAccountUseCase: GetCompanyUseCase,
    private val updateAccountUseCase: UpdateCompanyUseCase,
    private val fetchAccountUseCase: FetchCompanyUseCase,
    private val getTaxesUseCase: com.sales.app.domain.usecase.GetTaxesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CompanySettingsUiState())
    val uiState: StateFlow<CompanySettingsUiState> = _uiState.asStateFlow()
    
    fun loadAccount(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Load taxes
            launch {
                getTaxesUseCase().collect { taxes ->
                    _uiState.update { it.copy(taxes = taxes) }
                }
            }
            
            // Fetch from API first
            try {
                fetchAccountUseCase(accountId)
            } catch (e: Exception) {
                // Continue with local data if fetch fails
            }
            
            // Observe local data by ID
            getAccountUseCase(accountId).collect { account ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        account = account
                    ) 
                }
            }
        }
    }
    
    fun updateFinancialYearStart(dateTime: String) {
        val currentAccount = _uiState.value.account ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedAccount = currentAccount.copy(
                financialYearStart = dateTime
            )
            
            when (val result = updateAccountUseCase(updatedAccount)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            successMessage = "Financial year start updated successfully"
                        ) 
                    }
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
                    // Already handled by isSaving state
                }
            }
        }
    }
    
    fun updateTaxationType(taxationType: Int) {
        val currentAccount = _uiState.value.account ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedAccount = currentAccount.copy(
                taxationType = taxationType,
                // Reset default tax to 0 if switching to "No Tax"
                taxRate = if (taxationType == 1) 0 else currentAccount.taxRate
            )
            
            when (val result = updateAccountUseCase(updatedAccount)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            successMessage = "Taxation type updated successfully"
                        ) 
                    }
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
                    // Already handled by isSaving state
                }
            }
        }
    }
    
    fun updateDefaultTax(taxId: Int) {
        val currentAccount = _uiState.value.account ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedAccount = currentAccount.copy(
                defaultTaxId = taxId
            )
            
            when (val result = updateAccountUseCase(updatedAccount)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            successMessage = "Default tax updated successfully"
                        ) 
                    }
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
                    // Already handled by isSaving state
                }
            }
        }
    }
    
    fun updateAddress(address: String) {
        updateAccountField { it.copy(address = address) }
    }
    
    fun updateCall(call: String) {
        updateAccountField { it.copy(call = call) }
    }
    
    fun updateWhatsapp(whatsapp: String) {
        updateAccountField { it.copy(whatsapp = whatsapp) }
    }
    
    fun updateFooterContent(content: String) {
        updateAccountField { it.copy(footerContent = content) }
    }
    
    fun updateSignature(enabled: Boolean) {
        updateAccountField { it.copy(signature = if (enabled) "1" else "0") }
    }

    fun updateEnableDeliveryNotes(enabled: Boolean) {
        updateAccountField { it.copy(enableDeliveryNotes = enabled) }
    }

    fun updateEnableGrns(enabled: Boolean) {
        updateAccountField { it.copy(enableGrns = enabled) }
    }
    
    private fun updateAccountField(update: (Account) -> Account) {
        val currentAccount = _uiState.value.account ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedAccount = update(currentAccount)
            
            when (val result = updateAccountUseCase(updatedAccount)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            successMessage = "Settings updated successfully"
                        ) 
                    }
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
                    // Already handled by isSaving state
                }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
