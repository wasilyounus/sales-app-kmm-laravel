package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData? = null
)


@Serializable
data class AuthData(
    val user: UserDto,
    val token: String
)

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val created_at: String,
    val updated_at: String
)
