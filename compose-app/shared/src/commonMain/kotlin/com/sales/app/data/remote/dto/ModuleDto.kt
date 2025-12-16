package com.sales.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModuleDto(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    val icon: String,
    
    @SerialName("bg_color")
    val bgColor: String,
    
    val color: String,
    
    @SerialName("is_enabled")
    val isEnabled: Int, // Backend returns 1/0 for boolean in MySQL/Laravel sometimes, check or handle boolean
    
    @SerialName("sort_order")
    val sortOrder: Int
)
