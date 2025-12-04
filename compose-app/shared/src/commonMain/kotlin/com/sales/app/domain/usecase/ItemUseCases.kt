package com.sales.app.domain.usecase

import com.sales.app.data.repository.ItemRepository
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Uqc
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetItemsUseCase(
    private val itemRepository: ItemRepository
) {
    operator fun invoke(accountId: Int): Flow<List<Item>> {
        return itemRepository.getItemsByAccount(accountId)
    }
}

class SearchItemsUseCase(
    private val itemRepository: ItemRepository
) {
    operator fun invoke(accountId: Int, query: String): Flow<List<Item>> {
        return if (query.isBlank()) {
            itemRepository.getItemsByAccount(accountId)
        } else {
            itemRepository.searchItems(accountId, query)
        }
    }
}

class GetItemByIdUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(accountId: Int, itemId: Int): Item? {
        return itemRepository.getItemById(accountId, itemId)
    }
}

class CreateItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        taxId: Int?
    ): Result<Item> {
        return itemRepository.createItem(
            accountId = accountId,
            name = name,
            altName = altName,
            brand = brand,
            size = size,
            uqc = uqc,
            hsn = hsn,
            taxId = taxId
        )
    }
}

class UpdateItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(
        itemId: Int,
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        accountId: Int,
        taxId: Int?
    ): Result<Item> {
        return itemRepository.updateItem(
            id = itemId,
            name = name,
            altName = altName,
            brand = brand,
            size = size,
            uqc = uqc,
            hsn = hsn,
            accountId = accountId,
            taxId = taxId
        )
    }
}

class GetUqcsUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(): List<Uqc> {
        return itemRepository.getUqcs()
    }
}
