package com.sales.app.presentation.items.model

import com.sales.app.domain.model.Item

/**
 * UI model for displaying items in the list
 * Matches the reference app's ItemsUiModel structure
 */
data class ItemUiModel(
    val itemId: Int,
    val itemName: String,
    val itemHsn: String,
    val itemSize: String,
    val itemUqc: String,
)

/**
 * Convert domain Item to UI model
 */
fun Item.toUiModel(uqcName: String = ""): ItemUiModel {
    return ItemUiModel(
        itemId = this.id,
        itemName = this.name,
        itemHsn = this.hsn?.toString() ?: "",
        itemSize = this.size ?: "",
        itemUqc = uqcName
    )
}

/**
 * Extension function to filter items based on search query
 */
fun List<ItemUiModel>.search(query: String): List<ItemUiModel> {
    if (query.isBlank()) return this
    
    val searchTerms = query.lowercase().split(" ").filter { it.isNotBlank() }
    
    return filter { item ->
        searchTerms.all { term ->
            item.itemName.lowercase().contains(term) ||
            item.itemHsn.lowercase().contains(term) ||
            item.itemSize.lowercase().contains(term) ||
            item.itemUqc.lowercase().contains(term)
        }
    }
}
