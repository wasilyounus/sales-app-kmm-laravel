package com.sales.app.di

import android.content.Context
import androidx.room.Room
import com.sales.app.data.local.AppDatabase
import com.sales.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sales_database"
        )
            .fallbackToDestructiveMigration(true) // For development only
            .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
    
    @Provides
    @Singleton
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()
    
    @Provides
    @Singleton
    fun provideItemDao(database: AppDatabase): ItemDao = database.itemDao()
    
    @Provides
    @Singleton
    fun providePartyDao(database: AppDatabase): PartyDao = database.partyDao()
    
    @Provides
    @Singleton
    fun provideQuoteDao(database: AppDatabase): QuoteDao = database.quoteDao()
    
    @Provides
    @Singleton
    fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()
    
    @Provides
    @Singleton
    fun provideSaleDao(database: AppDatabase): SaleDao = database.saleDao()
    
    @Provides
    @Singleton
    fun providePurchaseDao(database: AppDatabase): PurchaseDao = database.purchaseDao()
    
    @Provides
    @Singleton
    fun provideSyncDao(database: AppDatabase): SyncDao = database.syncDao()
}
