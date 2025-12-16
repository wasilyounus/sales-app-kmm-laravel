package com.sales.app.presentation.pricelists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.PriceList
import com.sales.app.domain.repository.PriceListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceListsViewModel(
    private val priceListRepository: PriceListRepository
) : ViewModel() {

    private val _priceLists = MutableStateFlow<List<PriceList>>(emptyList())
    val priceLists: StateFlow<List<PriceList>> = _priceLists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPriceLists(companyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                priceListRepository.getPriceLists(companyId).collect {
                    _priceLists.value = it
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPriceList(companyId: Int, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = priceListRepository.createPriceList(name, companyId)
            result.onSuccess {
                onSuccess()
                // Refresh list? Flow should update if repo updates local DB
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun deletePriceList(id: Long) {
        viewModelScope.launch {
            priceListRepository.deletePriceList(id)
        }
    }
}
