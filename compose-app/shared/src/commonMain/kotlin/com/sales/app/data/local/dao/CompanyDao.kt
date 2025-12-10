package com.sales.app.data.local.dao

import androidx.room.*
import com.sales.app.data.local.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM companies")
    fun getAllCompanies(): Flow<List<CompanyEntity>>
    
    @Query("SELECT * FROM companies WHERE id = :id")
    fun getCompanyById(id: Int): Flow<CompanyEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<CompanyEntity>)
    
    @Update
    suspend fun updateCompany(company: CompanyEntity)
    
    @Delete
    suspend fun deleteCompany(company: CompanyEntity)
    
    @Query("DELETE FROM companies")
    suspend fun deleteAllCompanies()
}
