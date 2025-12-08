package com.sales.app.data.repository

import com.sales.app.data.local.dao.*
import com.sales.app.data.local.entity.*
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.SyncType
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import com.sales.app.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val apiService: ApiService,
    private val itemDao: ItemDao,
    private val partyDao: PartyDao,
    private val addressDao: AddressDao,
    private val taxDao: TaxDao,
    private val uqcDao: UqcDao,
    private val saleDao: SaleDao,
    private val quoteDao: QuoteDao,
    private val quoteItemDao: QuoteItemDao,
    private val purchaseDao: PurchaseDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val transactionDao: TransactionDao,
    private val priceListDao: PriceListDao,
    private val syncDao: SyncDao
) : SyncRepository {
    
    override suspend fun sync(accountId: Int, types: List<SyncType>): Result<Unit> {
        return try {
            // Expand grouped types into individual types
            val expandedTypes = SyncType.expand(types)
            
            // Sync each type
            expandedTypes.forEach { type ->
                when (type) {
                    is SyncType.Items -> syncItems(accountId)
                    is SyncType.Parties -> syncParties(accountId)
                    is SyncType.Taxes -> syncTaxes(accountId)
                    is SyncType.Uqcs -> syncUqcs(accountId)
                    is SyncType.Sales -> syncSales(accountId)
                    is SyncType.Quotes -> syncQuotes(accountId)
                    is SyncType.Purchases -> syncPurchases(accountId)
                    is SyncType.Orders -> syncOrders(accountId)
                    is SyncType.Payments -> syncPayments(accountId)
                    is SyncType.PriceLists -> syncPriceLists(accountId)
                    // Grouped types should already be expanded
                    else -> {}
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    private suspend fun syncItems(accountId: Int) {
        val response = apiService.getItems(accountId)
        if (response.success) {
            val entities = response.data.map { dto ->
                ItemEntity(
                    id = dto.id,
                    name = dto.name,
                    altName = dto.alt_name,
                    brand = dto.brand,
                    size = dto.size,
                    uqc = dto.uqc,
                    hsn = dto.hsn,
                    accountId = dto.account_id,
                    taxId = dto.tax_id,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at,
                    deletedAt = dto.deleted_at
                )
            }
            itemDao.insertItems(entities)
        }
    }
    
    private suspend fun syncParties(accountId: Int) {
        val response = apiService.getParties(accountId)
        if (response.success) {
            val parties = response.data
            val partyEntities = parties.map { dto ->
                PartyEntity(
                    id = dto.id,
                    name = dto.name,
                    taxNumber = dto.taxNumber,
                    phone = dto.phone,
                    email = dto.email,
                    accountId = dto.account_id,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at,
                    deletedAt = dto.deleted_at
                )
            }
            partyDao.insertParties(partyEntities)
            
            // Sync addresses
            val addressEntities = parties.flatMap { party ->
                party.addresses?.map { addr ->
                    AddressEntity(
                        id = addr.id,
                        partyId = party.id,
                        accountId = party.account_id,
                        line1 = addr.line1,
                        line2 = addr.line2,
                        place = addr.place,
                        district = addr.district,
                        state = addr.state,
                        pincode = addr.pincode,
                        country = addr.country,
                        latitude = addr.latitude,
                        longitude = addr.longitude,
                        createdAt = addr.created_at ?: "",
                        updatedAt = addr.updated_at ?: ""
                    )
                } ?: emptyList()
            }
            addressDao.insertAddresses(addressEntities)
        }
    }
    
    private suspend fun syncTaxes(accountId: Int) {
        // Using existing sync logic from syncMasterData
        val lastSync = syncDao.getSyncTimestamp("master_data")
        val timestamp = lastSync?.timestamp ?: "1970-01-01 00:00:00"
        val response = apiService.syncMasterData(accountId, timestamp)
        
        if (response.success) {
            response.data.taxes?.let { taxes ->
                val entities = taxes.map { dto ->
                    TaxEntity(
                        id = dto.id,
                        schemeName = dto.scheme_name,
                        country = dto.country,
                        tax1Name = dto.tax1_name,
                        tax1Val = dto.tax1_val,
                        tax2Name = dto.tax2_name,
                        tax2Val = dto.tax2_val,
                        tax3Name = dto.tax3_name,
                        tax3Val = dto.tax3_val,
                        tax4Name = dto.tax4_name,
                        tax4Val = dto.tax4_val,
                        active = dto.active,
                        createdAt = dto.created_at ?: "",
                        updatedAt = dto.updated_at ?: ""
                    )
                }
                taxDao.insertAll(entities)
            }
        }
    }
    
    private suspend fun syncUqcs(accountId: Int) {
        val response = apiService.getUqcs()
        if (response.success) {
            val entities = response.data.map { dto ->
                com.sales.app.data.local.entity.UqcEntity(
                    id = dto.id,
                    code = dto.uqc,
                    name = dto.quantity ?: "",
                    active = dto.active,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: ""
                )
            }
            uqcDao.insertAll(entities)
        }
    }
    
    private suspend fun syncSales(accountId: Int) {
        val salesResponse = apiService.getSales(accountId)
        if (salesResponse.success) {
            val saleEntities = salesResponse.data.map { dto ->
                SaleEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    invoiceNo = dto.invoice_no,
                    accountId = dto.account_id,
                    taxId = dto.tax_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            saleDao.insertSales(saleEntities)
        }
        
        // Sync sale items
        val itemsResponse = apiService.getSaleItems(accountId)
        if (itemsResponse.success) {
            val itemEntities = itemsResponse.data.map { dto ->
                SaleItemEntity(
                    id = dto.id,
                    saleId = dto.sale_id,
                    itemId = dto.item_id,
                    price = dto.price,
                    qty = dto.qty,
                    taxId = dto.tax_id,
                    accountId = dto.account_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            saleDao.insertSaleItems(itemEntities)
        }
    }
    
    private suspend fun syncQuotes(accountId: Int) {
        val quotesResponse = apiService.getQuotes(accountId)
        if (quotesResponse.success) {
            val quoteEntities = quotesResponse.data.map { dto ->
                QuoteEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    accountId = dto.account_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            quoteDao.insertQuotes(quoteEntities)
        }
        
        // Sync quote items
        val itemsResponse = apiService.getQuoteItems(accountId)
        if (itemsResponse.success) {
            val itemEntities = itemsResponse.data.map { dto ->
                QuoteItemEntity(
                    id = dto.id,
                    quoteId = dto.quote_id,
                    itemId = dto.item_id,
                    price = dto.price,
                    qty = dto.qty,
                    taxId = dto.tax_id,
                    accountId = dto.account_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            quoteItemDao.insertQuoteItems(itemEntities)
        }
    }
    
    private suspend fun syncPurchases(accountId: Int) {
        val purchasesResponse = apiService.getPurchases(accountId)
        if (purchasesResponse.success) {
            val purchaseEntities = purchasesResponse.data.map { dto ->
                PurchaseEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    accountId = dto.account_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            purchaseDao.insertPurchases(purchaseEntities)
        }
        
        // Sync purchase items - check if API exists
        try {
            val itemsResponse = apiService.getPurchaseItems(accountId)
            if (itemsResponse.success) {
                val itemEntities = itemsResponse.data.map { dto ->
                    PurchaseItemEntity(
                        id = dto.id,
                        purchaseId = dto.purchase_id,
                        itemId = dto.item_id,
                        price = dto.price,
                        qty = dto.qty,
                        taxId = dto.tax_id,
                        accountId = dto.account_id,
                        logId = dto.log_id,
                        createdAt = dto.created_at ?: "",
                        updatedAt = dto.updated_at ?: "",
                        deletedAt = dto.deleted_at
                    )
                }
                purchaseDao.insertPurchaseItems(itemEntities)
            }
        } catch (e: Exception) {
            // Purchase items sync might not be available
        }
    }
    
    private suspend fun syncOrders(accountId: Int) {
        val ordersResponse = apiService.getOrders(accountId)
        if (ordersResponse.success) {
            val orderEntities = ordersResponse.data.map { dto ->
                OrderEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    accountId = dto.account_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            orderDao.insertOrders(orderEntities)
        }
        
        // Sync order items
        val itemsResponse = apiService.getOrderItems(accountId)
        if (itemsResponse.success) {
            val itemEntities = itemsResponse.data.map { dto ->
                OrderItemEntity(
                    id = dto.id,
                    orderId = dto.order_id,
                    itemId = dto.item_id,
                    price = dto.price,
                    qty = dto.qty,
                    taxId = dto.tax_id,
                    accountId = dto.account_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            orderDao.insertOrderItems(itemEntities)
        }
    }
    
    private suspend fun syncPayments(accountId: Int) {
        val response = apiService.getTransactions(accountId)
        val entities = response.data.map { dto ->
            TransactionEntity(
                id = dto.id,
                date = dto.date,
                amount = dto.amount,
                type = dto.type,
                debitCode = dto.debitCode,
                creditCode = dto.creditCode,
                comment = dto.comment,
                accountId = accountId,
                partyName = dto.partyName,
                isReceived = dto.isReceived
            )
        }
        transactionDao.insertTransactions(entities)
    }
    
    private suspend fun syncPriceLists(accountId: Int) {
        val response = apiService.getPriceLists(accountId)
        val entities = response.data.map { dto ->
            PriceListEntity(
                id = dto.id,
                name = dto.name,
                itemsCount = dto.itemsCount,
                accountId = accountId
            )
        }
        priceListDao.insertPriceLists(entities)
    }
    
    override suspend fun syncMasterData(accountId: Int): Result<Unit> {
        return sync(accountId, listOf(SyncType.AllMasterData))
    }
    
    override suspend fun fullSync(accountId: Int): Result<Unit> {
        return try {
            val lastSync = syncDao.getSyncTimestamp("master_data")
            val timestamp = lastSync?.timestamp ?: "1970-01-01 00:00:00"
            
            val response = apiService.syncMasterData(accountId, timestamp)
            
            if (response.success) {
                // Save items
                response.data.items?.let { items ->
                    val entities = items.map { dto ->
                        ItemEntity(
                            id = dto.id,
                            name = dto.name,
                            altName = dto.alt_name,
                            brand = dto.brand,
                            size = dto.size,
                            uqc = dto.uqc,
                            hsn = dto.hsn,
                            accountId = dto.account_id,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at,
                            deletedAt = dto.deleted_at
                        )
                    }
                    itemDao.insertItems(entities)
                }
                
                // Save parties
                response.data.parties?.let { parties ->
                    val entities = parties.map { dto ->
                        PartyEntity(
                            id = dto.id,
                            name = dto.name,
                            taxNumber = dto.taxNumber,
                            phone = dto.phone,
                            email = dto.email,
                            accountId = dto.account_id,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at,
                            deletedAt = dto.deleted_at
                        )
                    }
                    partyDao.insertParties(entities)
                }
                
                // Save taxes
                response.data.taxes?.let { taxes ->
                    val entities = taxes.map { dto ->
                        com.sales.app.data.local.entity.TaxEntity(
                            id = dto.id,
                            schemeName = dto.scheme_name,
                            country = dto.country,
                            tax1Name = dto.tax1_name,
                            tax1Val = dto.tax1_val,
                            tax2Name = dto.tax2_name,
                            tax2Val = dto.tax2_val,
                            tax3Name = dto.tax3_name,
                            tax3Val = dto.tax3_val,
                            tax4Name = dto.tax4_name,
                            tax4Val = dto.tax4_val,
                            active = dto.active,
                            createdAt = "",
                            updatedAt = ""
                        )
                    }
                    taxDao.insertAll(entities)
                }
                
                // Save uqcs
                response.data.uqcs?.let { uqcs ->
                    val entities = uqcs.map { dto ->
                        com.sales.app.data.local.entity.UqcEntity(
                            id = dto.id,
                            code = dto.uqc,
                            name = dto.quantity ?: "",
                            active = dto.active,
                            createdAt = dto.created_at ?: "",
                            updatedAt = dto.updated_at ?: ""
                        )
                    }
                    uqcDao.insertAll(entities)
                }
                
                // Update sync timestamp
                syncDao.insertSyncTimestamp(
                    SyncTimestampEntity(
                        key = "master_data",
                        timestamp = response.data.timestamp,
                        lastSyncedAt = Clock.System.now().toEpochMilliseconds()
                    )
                )
                
                Result.Success(Unit)
            } else {
                Result.Error("Sync failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Sync failed: ${e.message}", e)
        }
    }
    
    
    override fun getSyncStatus(): Flow<SyncTimestampEntity?> = flow {
        emit(syncDao.getSyncTimestamp("master_data"))
    }
}
