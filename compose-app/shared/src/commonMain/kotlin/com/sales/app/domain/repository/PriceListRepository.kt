package com.sales.app.domain.repository

import com.sales.app.domain.model.PriceList
import com.sales.app.domain.model.PriceListItem
import kotlinx.coroutines.flow.Flow

interface PriceListRepository {
    fun getPriceLists(accountId: Int): Flow<List<PriceList>>
    suspend fun getPriceList(id: Long): Result<PriceList>
    suspend fun createPriceList(name: String, accountId: Int): Result<Unit>
    suspend fun updatePriceListItems(priceListId: Long, items: List<PriceListItem>): Result<Unit>
    suspend fun deletePriceList(id: Long): Result<Unit>
}
