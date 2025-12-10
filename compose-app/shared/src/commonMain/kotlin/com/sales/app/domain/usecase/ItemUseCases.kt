package com.sales.app.domain.usecase

import com.sales.app.domain.repository.ItemRepository
import com.sales.app.domain.model.Item
import com.sales.app.domain.model.Uqc
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow

class GetItemsUseCase(
    private val itemRepository: ItemRepository
) {
    operator fun invoke(companyId: Int): Flow<List<Item>> {
        return itemRepository.getItemsByAccount(companyId)
    }
}

class SearchItemsUseCase(
    private val itemRepository: ItemRepository
) {
    operator fun invoke(companyId: Int, query: String): Flow<List<Item>> {
        return if (query.isBlank()) {
            itemRepository.getItemsByAccount(companyId)
        } else {
            itemRepository.searchItems(companyId, query)
        }
    }
}

class GetItemByIdUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(companyId: Int, itemId: Int): Item? {
        return itemRepository.getItemById(companyId, itemId)
    }
}

class CreateItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(
        companyId: Int,
        name: String,
        altName: String?,
        brand: String?,
        size: String?,
        uqc: Int,
        hsn: Int?,
        taxId: Int?
    ): Result<Item> {
        return itemRepository.createItem(
            companyId = companyId,
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
        companyId: Int,
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
            companyId = companyId,
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
