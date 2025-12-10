package com.sales.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sales.app.data.local.entity.ContactEntity

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("SELECT * FROM contacts WHERE companyId = :companyId")
    suspend fun getContactsForCompany(companyId: Int): List<ContactEntity>

    @Query("DELETE FROM contacts WHERE companyId = :companyId")
    suspend fun deleteContactsForCompany(companyId: Int)

    @Transaction
    suspend fun syncContacts(companyId: Int, contacts: List<ContactEntity>) {
        deleteContactsForCompany(companyId)
        insertContacts(contacts)
    }
    
    @Query("DELETE FROM contacts")
    suspend fun clearAll()
}
