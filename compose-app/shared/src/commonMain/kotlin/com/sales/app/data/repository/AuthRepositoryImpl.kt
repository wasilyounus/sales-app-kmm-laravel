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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

import com.sales.app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }
    
    override suspend fun login(email: String, password: String): Result<User> {
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
                    currentCompanyId = response.data.user.current_company_id,
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
                        email = response.data.user.email,
                        currentCompanyId = response.data.user.current_company_id
                    )
                )
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.message}", e)
        }
    }
    
    override suspend fun register(
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
                    currentCompanyId = response.data.user.current_company_id,
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
                        email = response.data.user.email,
                        currentCompanyId = response.data.user.current_company_id
                    )
                )
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            Result.Error("Registration failed: ${e.message}", e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
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
    
    override fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    override fun currentUser(): Flow<User?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]?.toIntOrNull()
        }.distinctUntilChanged().flatMapLatest { userId ->
            if (userId != null) {
                userDao.getUserById(userId).map { entity ->
                    entity?.let {
                        User(
                            id = it.id,
                            name = it.name,
                            email = it.email,
                            currentCompanyId = it.currentCompanyId
                        )
                    }
                }
            } else {
                flowOf(null)
            }
        }
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return getToken().first() != null
    }
    
    override suspend fun selectCompany(companyId: Int): Result<Unit> {
        return try {
            apiService.selectCompany(companyId)
            
            // Update local user
            val userId = dataStore.data.map { it[USER_ID_KEY] }.first()?.toIntOrNull()
            if (userId != null) {
                val userEntity = userDao.getUserById(userId).first()
                if (userEntity != null) {
                    userDao.insertUser(userEntity.copy(currentCompanyId = companyId))
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to select company")
        }
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
