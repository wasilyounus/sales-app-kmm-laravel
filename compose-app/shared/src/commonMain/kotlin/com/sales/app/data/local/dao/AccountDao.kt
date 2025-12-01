package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: Int): Flow<AccountEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)
    
    @Update
    suspend fun updateAccount(account: AccountEntity)
    
    @Delete
    suspend fun deleteAccount(account: AccountEntity)
    
    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccounts()
}
