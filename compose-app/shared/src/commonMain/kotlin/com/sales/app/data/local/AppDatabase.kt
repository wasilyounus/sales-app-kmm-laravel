package com.sales.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sales.app.data.local.dao.*
import com.sales.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        ItemEntity::class,
        PartyEntity::class,
        AddressEntity::class,
        UqcEntity::class,
        TaxEntity::class,
        QuoteEntity::class,
        QuoteItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        PurchaseEntity::class,
        PurchaseItemEntity::class,
        SyncTimestampEntity::class,
        StockMovementEntity::class,
        TransactionEntity::class,
        PriceListEntity::class,
        PriceListItemEntity::class,
        DeliveryNoteEntity::class,
        DeliveryNoteItemEntity::class,
        GrnEntity::class,
        GrnItemEntity::class
    ],
    version = 14,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun itemDao(): ItemDao
    abstract fun partyDao(): PartyDao
    abstract fun quoteDao(): QuoteDao
    abstract fun quoteItemDao(): QuoteItemDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun purchaseItemDao(): PurchaseItemDao
    abstract fun addressDao(): AddressDao
    abstract fun syncDao(): SyncDao
    abstract fun taxDao(): TaxDao
    abstract fun uqcDao(): UqcDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun priceListDao(): PriceListDao
    abstract fun deliveryNoteDao(): DeliveryNoteDao
    abstract fun grnDao(): GrnDao
}
