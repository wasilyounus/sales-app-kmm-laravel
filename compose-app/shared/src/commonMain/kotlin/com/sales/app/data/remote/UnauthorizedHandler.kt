package com.sales.app.data.remote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sales.app.data.local.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnauthorizedHandler(
    private val dataStore: DataStore<Preferences>,
    private val userDao: UserDao,
    private val onUnauthorized: () -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun handle() {
        scope.launch {
            clearAuthData()
            onUnauthorized()
        }
    }
    
    private suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey("auth_token"))
            preferences.remove(stringPreferencesKey("user_id"))
        }
        userDao.deleteAllUsers()
    }
}
