package com.sales.app.domain.repository

import com.sales.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getTransactions(companyId: Int, page: Int = 1, search: String? = null): Flow<List<Transaction>>
    suspend fun createTransaction(transaction: Transaction): Result<Unit>
}
