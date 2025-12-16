package com.sales.app.domain.repository

import com.sales.app.data.remote.dto.OrderItemRequest
import com.sales.app.domain.model.Order
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrdersByAccount(companyId: Int): Flow<List<Order>>
    fun getOrderById(orderId: Int): Flow<Order?>
    suspend fun syncOrders(accountId: Int): Result<Unit>
    suspend fun createOrder(
        partyId: Int,
        date: String,
        items: List<OrderItemRequest>,
        accountId: Int,
        orderNo: String? = null
    ): Result<Order>
    suspend fun updateOrder(
        id: Int,
        partyId: Int,
        date: String,
        items: List<OrderItemRequest>,
        accountId: Int,
        orderNo: String? = null
    ): Result<Order>
    suspend fun deleteOrder(id: Int): Result<Unit>
}
