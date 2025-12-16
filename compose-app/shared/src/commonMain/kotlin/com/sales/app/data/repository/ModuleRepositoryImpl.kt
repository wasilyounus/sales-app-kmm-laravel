package com.sales.app.data.repository

import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.Module
import com.sales.app.domain.repository.ModuleRepository

class ModuleRepositoryImpl(
    private val apiService: ApiService
) : ModuleRepository {
    override suspend fun getModules(): List<Module> {
        return apiService.getModules().map { dto ->
            Module(
                id = dto.id,
                name = dto.name,
                slug = dto.slug,
                description = dto.description,
                icon = dto.icon,
                bgColor = dto.bgColor,
                color = dto.color,
                isEnabled = dto.isEnabled == 1 || dto.isEnabled.toString() == "true", // Handle questionable boolean serialization
                sortOrder = dto.sortOrder
            )
        }
    }
}
