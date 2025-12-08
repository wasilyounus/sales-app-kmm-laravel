package com.sales.app.domain.model

/**
 * Represents different types of data that can be synced.
 * Use this sealed class to specify which entities to sync from the API to local database.
 */
sealed class SyncType {
    // Master Data
    data object Items : SyncType()
    data object Parties : SyncType()
    data object Taxes : SyncType()
    data object Uqcs : SyncType()
    
    // Transaction Data
    data object Sales : SyncType()
    data object Quotes : SyncType()
    data object Purchases : SyncType()
    data object Orders : SyncType()
    data object Payments : SyncType()  // aka Transactions in the API
    data object PriceLists : SyncType()
    
    // Convenience groupings
    data object AllMasterData : SyncType()
    data object AllTransactionData : SyncType()
    data object Everything : SyncType()
    
    companion object {
        /**
         * Expands grouped sync types into individual types.
         * For example, AllMasterData expands to [Items, Parties, Taxes, Uqcs]
         */
        fun expand(types: List<SyncType>): List<SyncType> {
            return types.flatMap { type ->
                when (type) {
                    AllMasterData -> listOf(Items, Parties, Taxes, Uqcs)
                    AllTransactionData -> listOf(Sales, Quotes, Purchases, Orders, Payments, PriceLists)
                    Everything -> listOf(Items, Parties, Taxes, Uqcs, Sales, Quotes, Purchases, Orders, Payments, PriceLists)
                    else -> listOf(type)
                }
            }.distinct()
        }
    }
}
