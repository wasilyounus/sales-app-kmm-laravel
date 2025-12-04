package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE orderId = :orderId AND deletedAt IS NULL")
    fun getOrderItemsByOrderId(orderId: Int): Flow<List<OrderItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>)
    
    @Update
    suspend fun updateOrderItem(orderItem: OrderItemEntity)
    
    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItemEntity)
    
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItemsByOrderId(orderId: Int)
    
    @Query("DELETE FROM order_items WHERE accountId = :accountId")
    suspend fun deleteOrderItemsByAccount(accountId: Int)
}
