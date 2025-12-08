package com.sales.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sales.app.data.local.entity.UqcEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UqcDao {
    @Query("SELECT * FROM uqcs WHERE active = 1")
    fun getAllActive(): Flow<List<UqcEntity>>
    
    @Query("SELECT * FROM uqcs")
    fun getAll(): Flow<List<UqcEntity>>
    
    @Query("SELECT * FROM uqcs WHERE id = :id")
    suspend fun getById(id: Int): UqcEntity?
    
    @Query("SELECT * FROM uqcs WHERE code = :code")
    suspend fun getByCode(code: String): UqcEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(uqcs: List<UqcEntity>)
    
    @Query("DELETE FROM uqcs")
    suspend fun deleteAll()
}
