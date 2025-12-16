package com.sales.app.presentation.pricelists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.PriceList
import com.sales.app.domain.model.PriceListItem
import com.sales.app.domain.repository.ItemRepository
import com.sales.app.domain.repository.PriceListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceListDetailViewModel(
    private val priceListRepository: PriceListRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _priceList = MutableStateFlow<PriceList?>(null)
    val priceList: StateFlow<PriceList?> = _priceList.asStateFlow()

    private val _availableItems = MutableStateFlow<List<Item>>(emptyList())
    val availableItems: StateFlow<List<Item>> = _availableItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPriceList(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = priceListRepository.getPriceList(id)
            result.onSuccess {
                _priceList.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun loadAvailableItems(companyId: Int) {
        viewModelScope.launch {
            itemRepository.getItemsByAccount(companyId).collect {
                _availableItems.value = it
            }
        }
    }

    fun addItem(priceListId: Long, itemId: Long, price: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentItems = _priceList.value?.items ?: emptyList()
            // Check if item already exists, update if so
            val existingItem = currentItems.find { it.itemId == itemId }
            
            val newItem = PriceListItem(
                id = existingItem?.id ?: 0, // 0 for new
                priceListId = priceListId,
                itemId = itemId,
                price = price
            )
            
            val updatedItems = if (existingItem != null) {
                currentItems.map { if (it.itemId == itemId) newItem else it }
            } else {
                currentItems + newItem
            }

            val result = priceListRepository.updatePriceListItems(priceListId, updatedItems)
            result.onSuccess {
                loadPriceList(priceListId) // Refresh
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun removeItem(priceListId: Long, itemId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentItems = _priceList.value?.items ?: emptyList()
            val updatedItems = currentItems.filter { it.itemId != itemId }
            
            val result = priceListRepository.updatePriceListItems(priceListId, updatedItems)
            result.onSuccess {
                loadPriceList(priceListId) // Refresh
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
