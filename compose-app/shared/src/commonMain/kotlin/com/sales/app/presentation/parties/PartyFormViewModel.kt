package com.sales.app.presentation.parties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.data.remote.dto.AddressRequest
import com.sales.app.domain.usecase.CreatePartyUseCase
import com.sales.app.domain.usecase.GetPartyByIdUseCase
import com.sales.app.domain.usecase.UpdatePartyUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class PartyFormUiState {
    object Add : PartyFormUiState()
    object Update : PartyFormUiState()
}

data class AddressState(
    val id: Int = 0, // 0 for new
    val line1: String = "",
    val line2: String = "",
    val place: String = "",
    val state: String = "",
    val pincode: String = "",
    val country: String = "India"
)

data class PartyFormState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val formUiState: PartyFormUiState = PartyFormUiState.Add,
    
    // Fields
    val name: String = "",
    val taxNumber: String = "",
    val phone: String = "",
    val email: String = "",
    val addresses: List<AddressState> = emptyList(),
    
    // Validation
    val isNameValid: Boolean = true
)

class PartyFormViewModel(
    private val createPartyUseCase: CreatePartyUseCase,
    private val updatePartyUseCase: UpdatePartyUseCase,
    private val getPartyByIdUseCase: GetPartyByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartyFormState())
    val uiState: StateFlow<PartyFormState> = _uiState.asStateFlow()
    
    private var currentPartyId: Int? = null

    fun loadParty(companyId: Int, partyId: Int) {
        currentPartyId = partyId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, formUiState = PartyFormUiState.Update) }
            
            getPartyByIdUseCase(companyId, partyId).collect { party ->
                if (party != null) {
                    val addressStates = party.addresses.map { addr ->
                        AddressState(
                            id = addr.id,
                            line1 = addr.line1,
                            line2 = addr.line2 ?: "",
                            place = addr.place,
                            state = addr.state,
                            pincode = addr.pincode,
                            country = addr.country
                        )
                    }
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = party.name,
                            taxNumber = party.taxNumber ?: "",
                            phone = party.phone ?: "",
                            email = party.email ?: "",
                            addresses = addressStates
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "Failed to load party") 
                    }
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

    fun onTaxNumberChange(taxNumber: String) {
        _uiState.update { it.copy(taxNumber = taxNumber) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }
    
    // Address Management
    fun addAddress() {
        _uiState.update { 
            it.copy(addresses = it.addresses + AddressState())
        }
    }
    
    fun removeAddress(index: Int) {
        _uiState.update { 
            val newAddresses = it.addresses.toMutableList()
            if (index in newAddresses.indices) {
                newAddresses.removeAt(index)
            }
            it.copy(addresses = newAddresses)
        }
    }
    
    fun updateAddress(index: Int, address: AddressState) {
        _uiState.update {
            val newAddresses = it.addresses.toMutableList()
            if (index in newAddresses.indices) {
                newAddresses[index] = address
            }
            it.copy(addresses = newAddresses)
        }
    }

    fun saveParty(companyId: Int, onSuccess: () -> Unit) {
        if (!validateForm()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            val addressRequests = _uiState.value.addresses.map { addr ->
                AddressRequest(
                    line1 = addr.line1,
                    line2 = addr.line2.ifBlank { null },
                    place = addr.place,
                    state = addr.state,
                    pincode = addr.pincode,
                    country = addr.country
                )
            }
            
            val result = if (_uiState.value.formUiState is PartyFormUiState.Add) {
                createPartyUseCase(
                    companyId = companyId,
                    name = _uiState.value.name,
                    taxNumber = _uiState.value.taxNumber.ifBlank { null },
                    phone = _uiState.value.phone.ifBlank { null },
                    email = _uiState.value.email.ifBlank { null },
                    addresses = addressRequests
                )
            } else {
                currentPartyId?.let { id ->
                    updatePartyUseCase(
                        id = id,
                        name = _uiState.value.name,
                        taxNumber = _uiState.value.taxNumber.ifBlank { null },
                        phone = _uiState.value.phone.ifBlank { null },
                        email = _uiState.value.email.ifBlank { null },
                        addresses = addressRequests,
                        companyId = companyId
                    )
                } ?: Result.Error("Party ID not found")
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
                            error = result.message ?: "Failed to save party"
                        ) 
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isSaving = true) }
                }
            }
        }
    }
    
    fun saveAndAdd(companyId: Int) {
        saveParty(companyId) {
            resetForm()
        }
    }
    
    private fun resetForm() {
        _uiState.update { 
            it.copy(
                name = "",
                taxNumber = "",
                phone = "",
                email = "",
                addresses = emptyList(),
                isNameValid = true,
                formUiState = PartyFormUiState.Add
            ) 
        }
        currentPartyId = null
    }

    private fun validateForm(): Boolean {
        val isNameValid = _uiState.value.name.isNotBlank()
        // Validate addresses if needed (e.g. required fields)
        val areAddressesValid = _uiState.value.addresses.all { 
            it.line1.isNotBlank() && it.place.isNotBlank() && it.state.isNotBlank() && it.pincode.isNotBlank()
        }
        
        if (!areAddressesValid) {
             _uiState.update { it.copy(error = "Please fill all required address fields") }
             return false
        }
        
        _uiState.update { it.copy(isNameValid = isNameValid) }
        return isNameValid
    }
}
