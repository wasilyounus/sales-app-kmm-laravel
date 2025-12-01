package com.sales.app.domain.usecase

import com.sales.app.data.repository.AuthRepository
import com.sales.app.util.Result

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
