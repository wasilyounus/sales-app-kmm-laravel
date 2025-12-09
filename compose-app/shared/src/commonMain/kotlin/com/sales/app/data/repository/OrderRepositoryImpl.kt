package com.sales.app.data.repository

import com.sales.app.data.local.dao.OrderDao
import com.sales.app.data.local.dao.OrderItemDao
import com.sales.app.data.local.entity.OrderEntity
import com.sales.app.data.local.entity.OrderItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.OrderItemRequest
import com.sales.app.data.remote.dto.OrderRequest
import com.sales.app.domain.model.Order
import com.sales.app.domain.model.OrderItem
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

import com.sales.app.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val apiService: ApiService,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) : OrderRepository {
    override fun getOrdersByAccount(accountId: Int): Flow<List<Order>> {
        return orderDao.getOrdersByAccount(accountId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getOrderById(orderId: Int): Flow<Order?> {
        val orderFlow = orderDao.getOrderById(orderId)
        val itemsFlow = orderItemDao.getOrderItemsByOrderId(orderId)
        
        return combine(orderFlow, itemsFlow) { orderEntity, itemEntities ->
            orderEntity?.toDomainModel(itemEntities.map { it.toDomainModel() })
        }
    }
    
    override suspend fun syncOrders(accountId: Int): Result<Unit> {
        return try {
            val response = apiService.getOrders(accountId)
            
            if (response.success) {
                val entities = response.data.map { dto ->
                    OrderEntity(
                        id = dto.id,
                        partyId = dto.party_id,
                        date = dto.date,
                        orderNo = dto.order_no,
                        accountId = dto.account_id,
                        createdAt = dto.created_at ?: "",
                        updatedAt = dto.updated_at ?: "",
                        deletedAt = dto.deleted_at
                    )
                }
                orderDao.insertOrders(entities)
                
                // Also sync items
                syncOrderItems(accountId)
                
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync orders")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    private suspend fun syncOrderItems(accountId: Int) {
        try {
            val response = apiService.getOrderItems(accountId)
            if (response.success) {
                val entities = response.data.map { dto ->
                    OrderItemEntity(
                        id = dto.id,
                        orderId = dto.order_id,
                        itemId = dto.item_id,
                        price = dto.price,
                        qty = dto.qty,
                        taxId = dto.tax_id,
                        accountId = dto.account_id,
                        logId = dto.log_id,
                        createdAt = dto.created_at ?: "",
                        updatedAt = dto.updated_at ?: "",
                        deletedAt = dto.deleted_at
                    )
                }
                orderItemDao.insertOrderItems(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override suspend fun createOrder(
        partyId: Int,
        date: String,
        items: List<OrderItemRequest>,
        accountId: Int,
        orderNo: String?
    ): Result<Order> {
        return try {
            val request = OrderRequest(
                party_id = partyId,
                date = date,
                order_no = orderNo,
                account_id = accountId,
                items = items
            )
            val response = apiService.createOrder(request)
            
            if (response.success) {
                val dto = response.data
                val entity = OrderEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    orderNo = dto.order_no,
                    accountId = dto.account_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
                orderDao.insertOrder(entity)
                
                // Save items from response if present
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        OrderItemEntity(
                            id = itemDto.id,
                            orderId = itemDto.order_id,
                            itemId = itemDto.item_id,
                            price = itemDto.price,
                            qty = itemDto.qty,
                            taxId = itemDto.tax_id,
                            accountId = itemDto.account_id,
                            logId = itemDto.log_id,
                            createdAt = dto.created_at ?: "",
                            updatedAt = dto.updated_at ?: "",
                            deletedAt = itemDto.deleted_at
                        )
                    }
                    orderItemDao.insertOrderItems(itemEntities)
                }
                
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to create order")
            }
        } catch (e: Exception) {
            Result.Error("Create failed: ${e.message}", e)
        }
    }
    
    override suspend fun updateOrder(
        id: Int,
        partyId: Int,
        date: String,
        items: List<OrderItemRequest>,
        accountId: Int,
        orderNo: String?
    ): Result<Order> {
        return try {
            val request = OrderRequest(
                party_id = partyId,
                date = date,
                order_no = orderNo,
                account_id = accountId,
                items = items
            )
            val response = apiService.updateOrder(id, request)
            
            if (response.success) {
                val dto = response.data
                val entity = OrderEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    orderNo = dto.order_no,
                    accountId = dto.account_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
                orderDao.updateOrder(entity)
                
                // Save items from response if present
                dto.items?.let { items ->
                    val itemEntities = items.map { itemDto ->
                        OrderItemEntity(
                            id = itemDto.id,
                            orderId = itemDto.order_id,
                            itemId = itemDto.item_id,
                            price = itemDto.price,
                            qty = itemDto.qty,
                            taxId = itemDto.tax_id,
                            accountId = itemDto.account_id,
                            logId = itemDto.log_id,
                            createdAt = dto.created_at ?: "",
                            updatedAt = dto.updated_at ?: "",
                            deletedAt = itemDto.deleted_at
                        )
                    }
                    orderItemDao.insertOrderItems(itemEntities)
                }
                
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error("Failed to update order")
            }
        } catch (e: Exception) {
            Result.Error("Update failed: ${e.message}", e)
        }
    }
    
    override suspend fun deleteOrder(id: Int): Result<Unit> {
        return try {
            apiService.deleteOrder(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Delete failed: ${e.message}", e)
        }
    }
    
    private fun OrderEntity.toDomainModel(items: List<OrderItem> = emptyList()) = Order(
        id = id,
        partyId = partyId,
        date = date,
        orderNo = orderNo,
        accountId = accountId,
        items = items
    )
    
    private fun OrderItemEntity.toDomainModel() = OrderItem(
        id = id,
        orderId = orderId,
        itemId = itemId,
        price = price,
        qty = qty,
        accountId = accountId
    )
}
