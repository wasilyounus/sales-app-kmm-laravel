package com.sales.app.domain.usecase

import com.sales.app.data.repository.AuthRepository
import com.sales.app.domain.model.User
import com.sales.app.util.Result

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<User> {
        // Validate inputs
        if (name.isBlank()) {
            return Result.Error("Name cannot be empty")
        }
        if (email.isBlank()) {
            return Result.Error("Email cannot be empty")
        }
        if (!emailRegex.matches(email)) {
            return Result.Error("Invalid email format")
        }
        if (password.isBlank()) {
            return Result.Error("Password cannot be empty")
        }
        if (password.length < 6) {
            return Result.Error("Password must be at least 6 characters")
        }
        if (password != passwordConfirmation) {
            return Result.Error("Passwords do not match")
        }
        
        return authRepository.register(name, email, password, passwordConfirmation)
    }
}
