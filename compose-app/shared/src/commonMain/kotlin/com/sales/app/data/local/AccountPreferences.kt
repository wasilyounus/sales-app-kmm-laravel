package com.sales.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountPreferences(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        private val CURRENT_ACCOUNT_ID = intPreferencesKey("current_account_id")
    }
    
    /**
     * Save the current account ID
     */
    suspend fun saveCurrentAccount(accountId: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_ACCOUNT_ID] = accountId
        }
    }
    
    /**
     * Get the current account ID as a Flow
     */
    fun getCurrentAccount(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[CURRENT_ACCOUNT_ID]
        }
    }
    
    /**
     * Clear the current account
     */
    suspend fun clearCurrentAccount() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_ACCOUNT_ID)
        }
    }
}
