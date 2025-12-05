package com.sales.app.domain.repository

import com.sales.app.domain.model.User
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<User>
    suspend fun logout(): Result<Unit>
    fun getToken(): Flow<String?>
    suspend fun isLoggedIn(): Boolean
}
