package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE partyId = :partyId")
    fun getAddressesByPartyId(partyId: Int): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE accountId = :accountId")
    fun getAddressesByAccount(accountId: Int): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE partyId = :partyId")
    suspend fun deleteAddressesByPartyId(partyId: Int)
}
