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
    val company: Company? = null,
    val taxes: List<com.sales.app.domain.model.Tax> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class CompanySettingsViewModel(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val fetchCompanyUseCase: FetchCompanyUseCase,
    private val getTaxesUseCase: com.sales.app.domain.usecase.GetTaxesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CompanySettingsUiState())
    val uiState: StateFlow<CompanySettingsUiState> = _uiState.asStateFlow()
    
    fun loadCompany(companyId: Int) {
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
                fetchCompanyUseCase(companyId)
            } catch (e: Exception) {
                // Continue with local data if fetch fails
            }
            
            // Observe local data by ID
            getCompanyUseCase(companyId).collect { company ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        company = company
                    ) 
                }
            }
        }
    }
    
    fun updateFinancialYearStart(dateTime: String) {
        val currentCompany = _uiState.value.company ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedCompany = currentCompany.copy(
                financialYearStart = dateTime
            )
            
            when (val result = updateCompanyUseCase(updatedCompany)) {
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
        val currentCompany = _uiState.value.company ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedCompany = currentCompany.copy(
                taxationType = taxationType,
                // Reset default tax to 0 if switching to "No Tax"
                taxRate = if (taxationType == 1) 0 else currentCompany.taxRate
            )
            
            when (val result = updateCompanyUseCase(updatedCompany)) {
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
        val currentCompany = _uiState.value.company ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedCompany = currentCompany.copy(
                defaultTaxId = taxId
            )
            
            when (val result = updateCompanyUseCase(updatedCompany)) {
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
        updateCompanyField { it.copy(address = address) }
    }
    
    fun updateCall(call: String) {
        updateCompanyField { it.copy(call = call) }
    }
    
    fun updateWhatsapp(whatsapp: String) {
        updateCompanyField { it.copy(whatsapp = whatsapp) }
    }
    
    fun updateFooterContent(content: String) {
        updateCompanyField { it.copy(footerContent = content) }
    }
    
    fun updateSignature(enabled: Boolean) {
        updateCompanyField { it.copy(signature = if (enabled) "1" else "0") }
    }

    fun updateEnableDeliveryNotes(enabled: Boolean) {
        updateCompanyField { it.copy(enableDeliveryNotes = enabled) }
    }

    fun updateEnableGrns(enabled: Boolean) {
        updateCompanyField { it.copy(enableGrns = enabled) }
    }

    fun updateDarkMode(enabled: Boolean) {
        updateCompanyField { it.copy(darkMode = enabled) }
    }
    
    private fun updateCompanyField(update: (Company) -> Company) {
        val currentCompany = _uiState.value.company ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            
            val updatedCompany = update(currentCompany)
            
            when (val result = updateCompanyUseCase(updatedCompany)) {
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
