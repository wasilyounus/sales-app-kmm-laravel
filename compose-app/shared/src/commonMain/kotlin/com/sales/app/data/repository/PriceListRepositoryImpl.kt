package com.sales.app.data.repository

import com.sales.app.data.local.dao.PriceListDao
import com.sales.app.data.local.entity.PriceListEntity
import com.sales.app.data.local.entity.PriceListItemEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.data.remote.dto.PriceListItemUpdateDto
import com.sales.app.data.remote.dto.PriceListRequest
import com.sales.app.data.remote.dto.UpdatePriceListItemsRequest
import com.sales.app.domain.model.PriceList
import com.sales.app.domain.model.PriceListItem
import com.sales.app.domain.repository.PriceListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PriceListRepositoryImpl(
    private val apiService: ApiService,
    private val priceListDao: PriceListDao
) : PriceListRepository {

    override fun getPriceLists(accountId: Int): Flow<List<PriceList>> {
        return priceListDao.getPriceLists(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPriceList(id: Long): Result<PriceList> {
        return try {
            // Try local first
            val local = priceListDao.getPriceList(id)
            if (local != null) {
                val items = priceListDao.getPriceListItems(id)
                return Result.success(local.toDomain(items))
            }
            
            // Fetch from API
            val remote = apiService.getPriceList(id)
            // Cache it? Ideally yes.
            Result.success(PriceList(
                id = remote.id,
                name = remote.name,
                itemsCount = remote.itemsCount,
                items = remote.items.map { 
                    PriceListItem(
                        id = it.id,
                        priceListId = it.priceListId,
                        itemId = it.itemId,
                        price = it.price,
                        itemName = it.itemName,
                        itemCode = it.itemCode,
                        standardPrice = it.standardPrice
                    ) 
                }
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPriceList(name: String, accountId: Int): Result<Unit> {
        return try {
            apiService.createPriceList(PriceListRequest(name))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePriceListItems(priceListId: Long, items: List<PriceListItem>): Result<Unit> {
        return try {
            val request = UpdatePriceListItemsRequest(
                items = items.map { 
                    PriceListItemUpdateDto(
                        itemId = it.itemId,
                        price = it.price
                    )
                }
            )
            apiService.updatePriceListItems(priceListId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePriceList(id: Long): Result<Unit> {
        return try {
            apiService.deletePriceList(id)
            // Update local
            priceListDao.deletePriceList(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun PriceListEntity.toDomain(items: List<PriceListItemEntity> = emptyList()): PriceList {
        return PriceList(
            id = id,
            name = name,
            itemsCount = itemsCount,
            items = items.map { it.toDomain() }
        )
    }

    private fun PriceListItemEntity.toDomain(): PriceListItem {
        return PriceListItem(
            id = id,
            priceListId = priceListId,
            itemId = itemId,
            price = price,
            itemName = itemName,
            itemCode = itemCode,
            standardPrice = standardPrice
        )
    }
}
