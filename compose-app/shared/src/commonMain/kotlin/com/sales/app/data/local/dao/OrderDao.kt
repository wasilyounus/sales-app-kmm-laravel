package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.OrderEntity
import com.sales.app.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE companyId = :companyId AND deletedAt IS NULL ORDER BY date DESC")
    fun getOrdersByAccount(companyId: Int): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE id = :id")
    fun getOrderById(id: Int): Flow<OrderEntity?>
    
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)
    
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    @Delete
    suspend fun deleteOrder(order: OrderEntity)
    
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: Int)
    
    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        val orderId = insertOrder(order)
        insertOrderItems(items)
    }
}
