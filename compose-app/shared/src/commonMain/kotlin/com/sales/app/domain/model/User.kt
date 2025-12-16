package com.sales.app.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val currentCompanyId: Int? = null
)
