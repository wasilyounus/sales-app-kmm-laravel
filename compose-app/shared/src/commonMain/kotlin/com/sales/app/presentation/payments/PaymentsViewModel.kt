package com.sales.app.presentation.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.Transaction
import com.sales.app.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentsViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadTransactions(companyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                paymentRepository.getTransactions(companyId).collect {
                    _transactions.value = it
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTransaction(transaction: Transaction, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = paymentRepository.createTransaction(transaction)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
