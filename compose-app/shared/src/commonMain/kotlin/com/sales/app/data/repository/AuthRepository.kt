package com.sales.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sales.app.data.local.dao.UserDao
import com.sales.app.data.local.entity.UserEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.LoginRequest
import com.sales.app.data.remote.dto.RegisterRequest
import com.sales.app.domain.model.User
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }
    
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            
            if (response.success && response.data != null) {
                // Save token
                saveToken(response.data.token)
                
                // Save user to local database
                val userEntity = UserEntity(
                    id = response.data.user.id,
                    name = response.data.user.name,
                    email = response.data.user.email,
                    createdAt = response.data.user.created_at,
                    updatedAt = response.data.user.updated_at
                )
                userDao.insertUser(userEntity)
                
                // Save user ID
                saveUserId(response.data.user.id.toString())
                
                Result.Success(
                    User(
                        id = response.data.user.id,
                        name = response.data.user.name,
                        email = response.data.user.email
                    )
                )
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.message}", e)
        }
    }
    
    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<User> {
        return try {
            val response = apiService.register(
                RegisterRequest(name, email, password, passwordConfirmation)
            )
            
            if (response.success && response.data != null) {
                // Save token
                saveToken(response.data.token)
                
                // Save user to local database
                val userEntity = UserEntity(
                    id = response.data.user.id,
                    name = response.data.user.name,
                    email = response.data.user.email,
                    createdAt = response.data.user.created_at,
                    updatedAt = response.data.user.updated_at
                )
                userDao.insertUser(userEntity)
                
                // Save user ID
                saveUserId(response.data.user.id.toString())
                
                Result.Success(
                    User(
                        id = response.data.user.id,
                        name = response.data.user.name,
                        email = response.data.user.email
                    )
                )
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            Result.Error("Registration failed: ${e.message}", e)
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            apiService.logout()
            clearAuthData()
            Result.Success(Unit)
        } catch (e: Exception) {
            // Clear local data even if API call fails
            clearAuthData()
            Result.Success(Unit)
        }
    }
    
    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return getToken().first() != null
    }
    
    private suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }
    
    private suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }
    
    private suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
        userDao.deleteAllUsers()
    }
}
