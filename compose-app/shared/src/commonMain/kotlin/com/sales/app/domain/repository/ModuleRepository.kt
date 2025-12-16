package com.sales.app.domain.repository

import com.sales.app.domain.model.Module

interface ModuleRepository {
    suspend fun getModules(): List<Module>
}
