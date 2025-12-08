package com.sales.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaravelValidationError(
    val message: String,
    val errors: Map<String, List<String>>? = null
)
