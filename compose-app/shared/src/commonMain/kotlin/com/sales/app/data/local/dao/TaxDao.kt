package com.sales.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sales.app.data.local.entity.TaxEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxDao {
    @Query("SELECT * FROM taxes WHERE active = 1")
    fun getAllActive(): Flow<List<TaxEntity>>
    
    @Query("SELECT * FROM taxes")
    fun getAll(): Flow<List<TaxEntity>>
    
    @Query("SELECT * FROM taxes WHERE id = :id")
    suspend fun getById(id: Int): TaxEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taxes: List<TaxEntity>)
    
    @Query("DELETE FROM taxes")
    suspend fun deleteAll()
}
