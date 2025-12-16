package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.DeliveryNoteEntity
import com.sales.app.data.local.entity.DeliveryNoteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryNoteDao {
    @Query("SELECT * FROM delivery_notes WHERE companyId = :companyId AND deletedAt IS NULL ORDER BY date DESC")
    fun getDeliveryNotesByAccount(companyId: Int): Flow<List<DeliveryNoteEntity>>
    
    @Query("SELECT * FROM delivery_notes WHERE saleId = :saleId AND deletedAt IS NULL ORDER BY date DESC")
    fun getDeliveryNotesBySale(saleId: Int): Flow<List<DeliveryNoteEntity>>
    
    @Query("SELECT * FROM delivery_notes WHERE id = :id")
    fun getDeliveryNoteById(id: Int): Flow<DeliveryNoteEntity?>
    
    @Query("SELECT * FROM delivery_note_items WHERE deliveryNoteId = :deliveryNoteId")
    fun getDeliveryNoteItems(deliveryNoteId: Int): Flow<List<DeliveryNoteItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryNote(deliveryNote: DeliveryNoteEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryNotes(deliveryNotes: List<DeliveryNoteEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryNoteItems(items: List<DeliveryNoteItemEntity>)
    
    @Update
    suspend fun updateDeliveryNote(deliveryNote: DeliveryNoteEntity)
    
    @Delete
    suspend fun deleteDeliveryNote(deliveryNote: DeliveryNoteEntity)
    
    @Query("DELETE FROM delivery_note_items WHERE deliveryNoteId = :deliveryNoteId")
    suspend fun deleteDeliveryNoteItems(deliveryNoteId: Int)
    
    @Transaction
    suspend fun insertDeliveryNoteWithItems(deliveryNote: DeliveryNoteEntity, items: List<DeliveryNoteItemEntity>) {
        insertDeliveryNote(deliveryNote)
        insertDeliveryNoteItems(items)
    }
}
