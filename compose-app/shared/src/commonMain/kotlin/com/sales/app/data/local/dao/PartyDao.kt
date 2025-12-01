package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.PartyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyDao {
    @Query("SELECT * FROM parties WHERE accountId = :accountId AND deletedAt IS NULL ORDER BY name ASC")
    fun getPartiesByAccount(accountId: Int): Flow<List<PartyEntity>>
    
    @Query("SELECT * FROM parties WHERE id = :id")
    fun getPartyById(id: Int): Flow<PartyEntity?>
    
    @Query("SELECT * FROM parties WHERE accountId = :accountId AND name LIKE '%' || :query || '%' AND deletedAt IS NULL")
    fun searchParties(accountId: Int, query: String): Flow<List<PartyEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: PartyEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParties(parties: List<PartyEntity>)
    
    @Update
    suspend fun updateParty(party: PartyEntity)
    
    @Delete
    suspend fun deleteParty(party: PartyEntity)
    
    @Query("DELETE FROM parties WHERE accountId = :accountId")
    suspend fun deletePartiesByAccount(accountId: Int)
}
