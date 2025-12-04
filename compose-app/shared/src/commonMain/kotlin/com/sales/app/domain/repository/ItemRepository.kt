package com.sales.app.domain.repository

import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Uqc
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsByAccount(accountId: Int): Flow<List<Item>>
    fun searchItems(accountId: Int, query: String): Flow<List<Item>>
    suspend fun syncItems(accountId: Int): Result<Unit>
    suspend fun createItem(
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        accountId: Int,
        taxId: Int? = null
    ): Result<Item>
    suspend fun updateItem(
        id: Int,
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        accountId: Int,
        taxId: Int? = null
    ): Result<Item>
    suspend fun deleteItem(id: Int): Result<Unit>
    suspend fun getItemById(accountId: Int, itemId: Int): Item?
    suspend fun getUqcs(): List<Uqc>
}
