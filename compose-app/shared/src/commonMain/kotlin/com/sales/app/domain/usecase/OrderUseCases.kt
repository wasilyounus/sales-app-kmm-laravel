package com.sales.app.domain.usecase

import com.sales.app.data.remote.dto.OrderItemRequest
import com.sales.app.domain.repository.OrderRepository
import com.sales.app.domain.model.Order
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(companyId: Int): Flow<List<Order>> {
        return orderRepository.getOrdersByAccount(companyId)
    }
}

class GetOrderByIdUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(orderId: Int): Flow<Order?> {
        return orderRepository.getOrderById(orderId)
    }
}

class CreateOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        partyId: Int,
        date: String,
        items: List<OrderItemRequest>,
        companyId: Int,
        orderNo: String? = null
    ): Result<Order> {
        return orderRepository.createOrder(partyId, date, items, companyId, orderNo)
    }
}

class UpdateOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        id: Int,
        partyId: Int,
        date: String,
        items: List<OrderItemRequest>,
        companyId: Int,
        orderNo: String? = null
    ): Result<Order> {
        return orderRepository.updateOrder(id, partyId, date, items, companyId, orderNo)
    }
}

class DeleteOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return orderRepository.deleteOrder(id)
    }
}

class SyncOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(companyId: Int): Result<Unit> {
        return orderRepository.syncOrders(companyId)
    }
}
