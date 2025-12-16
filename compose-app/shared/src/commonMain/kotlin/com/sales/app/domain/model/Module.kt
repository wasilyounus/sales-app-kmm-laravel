package com.sales.app.domain.model

data class Module(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    val icon: String,
    val bgColor: String,
    val color: String,
    val isEnabled: Boolean,
    val sortOrder: Int
)
