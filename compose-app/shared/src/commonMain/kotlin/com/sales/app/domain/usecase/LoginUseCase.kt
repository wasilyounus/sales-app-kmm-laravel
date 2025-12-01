package com.sales.app.domain.usecase

import com.sales.app.data.repository.AuthRepository
import com.sales.app.domain.model.User
import com.sales.app.util.Result

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validate inputs
        if (email.isBlank()) {
            return Result.Error("Email cannot be empty")
        }
        if (password.isBlank()) {
            return Result.Error("Password cannot be empty")
        }
        if (!emailRegex.matches(email)) {
            return Result.Error("Invalid email format")
        }
        
        return authRepository.login(email, password)
    }
}
