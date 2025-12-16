package com.sales.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CompanyPreferences(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        private val CURRENT_COMPANY_ID = intPreferencesKey("current_company_id")
    }
    
    /**
     * Save the current company ID
     */
    suspend fun saveCurrentCompany(companyId: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_COMPANY_ID] = companyId
        }
    }
    
    /**
     * Get the current company ID as a Flow
     */
    fun getCurrentCompany(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[CURRENT_COMPANY_ID]
        }
    }
    
    /**
     * Clear the current company
     */
    suspend fun clearCurrentCompany() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_COMPANY_ID)
        }
    }
}
