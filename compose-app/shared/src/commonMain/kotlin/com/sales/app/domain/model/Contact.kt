package com.sales.app.domain.model

data class Contact(
    val id: Int = 0,
    val name: String,
    val phone: String?,
    val email: String?,
    val designation: String?,
    val isPrimary: Boolean
)
