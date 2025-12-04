package com.sales.app.presentation.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Tax
import com.sales.app.domain.model.Uqc
import com.sales.app.domain.usecase.CreateItemUseCase
import com.sales.app.domain.usecase.GetItemByIdUseCase
import com.sales.app.domain.usecase.GetTaxesUseCase
import com.sales.app.domain.usecase.GetUqcsUseCase
import com.sales.app.domain.usecase.UpdateItemUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class FormUiState {
    object Add : FormUiState()
    object Update : FormUiState()
}

data class ItemFormState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val formUiState: FormUiState = FormUiState.Add,
    
    // Fields
    val name: String = "",
    val brand: String = "",
    val size: String = "",
    val hsn: String = "",
    val uqcId: Int = 0,
    
    // Data
    val uqcs: List<Uqc> = emptyList(),
    val taxes: List<Tax> = emptyList(),
    
    // Validation
    val isNameValid: Boolean = true,
    val isHsnValid: Boolean = true,
    
    // Tax Selection
    val selectedTaxId: Int? = null
)

class ItemFormViewModel(
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val getUqcsUseCase: GetUqcsUseCase,
    private val getTaxesUseCase: GetTaxesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemFormState())
    val uiState: StateFlow<ItemFormState> = _uiState.asStateFlow()
    
    private var currentItemId: Int? = null

    init {
        loadUqcs()
        loadTaxes()
    }

    private fun loadUqcs() {
        viewModelScope.launch {
            val uqcs = getUqcsUseCase()
            _uiState.update { 
                it.copy(
                    uqcs = uqcs,
                    uqcId = uqcs.firstOrNull()?.id ?: 0
                ) 
            }
        }
    }
    
    private fun loadTaxes() {
        viewModelScope.launch {
            getTaxesUseCase().collect { taxes ->
                _uiState.update { it.copy(taxes = taxes) }
            }
        }
    }

    fun loadItem(accountId: Int, itemId: Int) {
        currentItemId = itemId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, formUiState = FormUiState.Update) }
            val item = getItemByIdUseCase(accountId, itemId)
            
            if (item != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = item.name,
                        brand = item.brand ?: "",
                        size = item.size ?: "",
                        hsn = item.hsn?.toString() ?: "",
                        uqcId = item.uqc,
                        selectedTaxId = item.taxId
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Failed to load item") 
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { 
            it.copy(
                name = name,
                isNameValid = name.isNotBlank()
            ) 
        }
    }

    fun onBrandChange(brand: String) {
        _uiState.update { it.copy(brand = brand) }
    }

    fun onSizeChange(size: String) {
        _uiState.update { it.copy(size = size) }
    }

    fun onHsnChange(hsn: String) {
        // Only allow numeric input
        if (hsn.all { it.isDigit() }) {
            _uiState.update { it.copy(hsn = hsn) }
        }
    }

    fun onUqcChange(uqcId: Int) {
        _uiState.update { it.copy(uqcId = uqcId) }
    }
    
    fun onTaxChange(taxId: Int?) {
        _uiState.update { it.copy(selectedTaxId = taxId) }
    }

    fun saveItem(accountId: Int, onSuccess: () -> Unit) {
        if (!validateForm()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            val result = if (_uiState.value.formUiState is FormUiState.Add) {
                createItemUseCase(
                    accountId = accountId,
                    name = _uiState.value.name,
                    altName = null,
                    brand = _uiState.value.brand.ifBlank { null },
                    size = _uiState.value.size.ifBlank { null },
                    uqc = _uiState.value.uqcId,
                    hsn = _uiState.value.hsn.toIntOrNull(),
                    taxId = _uiState.value.selectedTaxId
                )
            } else {
                currentItemId?.let { id ->
                    updateItemUseCase(
                        itemId = id,
                        name = _uiState.value.name,
                        altName = null,
                        brand = _uiState.value.brand.ifBlank { null },
                        size = _uiState.value.size.ifBlank { null },
                        uqc = _uiState.value.uqcId,
                        hsn = _uiState.value.hsn.toIntOrNull(),
                        accountId = accountId,
                        taxId = _uiState.value.selectedTaxId
                    )
                } ?: Result.Error("Item ID not found")
            }
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            error = result.message ?: "Failed to save item"
                        ) 
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isSaving = true) }
                }
            }
        }
    }
    
    fun saveAndAdd(accountId: Int) {
        saveItem(accountId) {
            resetForm()
        }
    }
    
    private fun resetForm() {
        _uiState.update { 
            it.copy(
                name = "",
                brand = "",
                size = "",
                hsn = "",
                // Keep uqcId as is or reset to first
                isNameValid = true,
                isHsnValid = true,
                formUiState = FormUiState.Add
            ) 
        }
        currentItemId = null
    }

    private fun validateForm(): Boolean {
        val isNameValid = _uiState.value.name.isNotBlank()
        _uiState.update { it.copy(isNameValid = isNameValid) }
        return isNameValid
    }
}
