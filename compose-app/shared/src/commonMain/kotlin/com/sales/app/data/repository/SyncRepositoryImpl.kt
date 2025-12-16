@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.data.repository

import com.sales.app.data.local.dao.*
import com.sales.app.data.local.entity.*
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.SyncType
import com.sales.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
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
    
    override suspend fun sync(companyId: Int, types: List<SyncType>): Result<Unit> {
        return try {
            // Expand grouped types into individual types
            val expandedTypes = SyncType.expand(types)
            
            // Sync each type
            expandedTypes.forEach { type ->
                when (type) {
                    is SyncType.Items -> syncItems(companyId)
                    is SyncType.Parties -> syncParties(companyId)
                    is SyncType.Taxes -> syncTaxes(companyId)
                    is SyncType.Uqcs -> syncUqcs(companyId)
                    is SyncType.Sales -> syncSales(companyId)
                    is SyncType.Quotes -> syncQuotes(companyId)
                    is SyncType.Purchases -> syncPurchases(companyId)
                    is SyncType.Orders -> syncOrders(companyId)
                    is SyncType.Payments -> syncPayments(companyId)
                    is SyncType.PriceLists -> syncPriceLists(companyId)
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
    
    private suspend fun syncItems(companyId: Int) {
        val response = apiService.getItems(companyId)
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
                    companyId = dto.company_id,
                    taxId = dto.tax_id,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at,
                    deletedAt = dto.deleted_at
                )
            }
            itemDao.insertItems(entities)
        }
    }
    
    private suspend fun syncParties(companyId: Int) {
        val response = apiService.getParties(companyId)
        if (response.success) {
            val parties = response.data
            val partyEntities = parties.map { dto ->
                PartyEntity(
                    id = dto.id,
                    name = dto.name,
                    taxNumber = dto.taxNumber,
                    phone = dto.phone,
                    email = dto.email,
                    companyId = dto.company_id,
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
                        companyId = party.company_id ?: 0,
                        line1 = addr.line1 ?: "",
                        line2 = addr.line2,
                        place = addr.place ?: "",
                        district = addr.district,
                        state = addr.state ?: "",
                        pincode = addr.pincode ?: "",
                        country = addr.country ?: "",
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
    
    private suspend fun syncTaxes(companyId: Int) {
        // Using existing sync logic from syncMasterData
        val lastSync = syncDao.getSyncTimestamp("master_data")
        val timestamp = lastSync?.timestamp ?: "1970-01-01 00:00:00"
        val response = apiService.syncMasterData(companyId, timestamp)
        
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
    
    private suspend fun syncUqcs(companyId: Int) {
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
    
    private suspend fun syncSales(companyId: Int) {
        val salesResponse = apiService.getSales(companyId)
        if (salesResponse.success) {
            val saleEntities = salesResponse.data.map { dto ->
                SaleEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    invoiceNo = dto.invoice_no,
                    companyId = dto.company_id,
                    taxId = dto.tax_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            saleDao.insertSales(saleEntities)
        }
        
        // Sync sale items
        val itemsResponse = apiService.getSaleItems(companyId)
        if (itemsResponse.success) {
            val itemEntities = itemsResponse.data.map { dto ->
                SaleItemEntity(
                    id = dto.id,
                    saleId = dto.sale_id,
                    itemId = dto.item_id,
                    price = dto.price,
                    qty = dto.qty,
                    taxId = dto.tax_id,
                    companyId = dto.company_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            saleDao.insertSaleItems(itemEntities)
        }
    }
    
    private suspend fun syncQuotes(companyId: Int) {
        val quotesResponse = apiService.getQuotes(companyId)
        if (quotesResponse.success) {
            val quoteEntities = quotesResponse.data.map { dto ->
                QuoteEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    companyId = dto.company_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            quoteDao.insertQuotes(quoteEntities)
        }
        
        // Sync quote items
        val itemsResponse = apiService.getQuoteItems(companyId)
        if (itemsResponse.success) {
            val itemEntities = itemsResponse.data.map { dto ->
                QuoteItemEntity(
                    id = dto.id,
                    quoteId = dto.quote_id,
                    itemId = dto.item_id,
                    price = dto.price,
                    qty = dto.qty,
                    taxId = dto.tax_id,
                    companyId = dto.company_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            quoteItemDao.insertQuoteItems(itemEntities)
        }
    }
    
    private suspend fun syncPurchases(companyId: Int) {
        val purchasesResponse = apiService.getPurchases(companyId)
        if (purchasesResponse.success) {
            val purchaseEntities = purchasesResponse.data.map { dto ->
                PurchaseEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    companyId = dto.company_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            purchaseDao.insertPurchases(purchaseEntities)
        }
        
        // Sync purchase items - check if API exists
        try {
            val itemsResponse = apiService.getPurchaseItems(companyId)
            if (itemsResponse.success) {
                val itemEntities = itemsResponse.data.map { dto ->
                    PurchaseItemEntity(
                        id = dto.id,
                        purchaseId = dto.purchase_id,
                        itemId = dto.item_id,
                        price = dto.price,
                        qty = dto.qty,
                        taxId = dto.tax_id,
                        companyId = dto.company_id,
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
    
    private suspend fun syncOrders(companyId: Int) {
        val ordersResponse = apiService.getOrders(companyId)
        if (ordersResponse.success) {
            val orderEntities = ordersResponse.data.map { dto ->
                OrderEntity(
                    id = dto.id,
                    partyId = dto.party_id,
                    date = dto.date,
                    companyId = dto.company_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            orderDao.insertOrders(orderEntities)
        }
        
        // Sync order items
        val itemsResponse = apiService.getOrderItems(companyId)
        if (itemsResponse.success) {
            val itemEntities = itemsResponse.data.map { dto ->
                OrderItemEntity(
                    id = dto.id,
                    orderId = dto.order_id,
                    itemId = dto.item_id,
                    price = dto.price,
                    qty = dto.qty,
                    taxId = dto.tax_id,
                    companyId = dto.company_id,
                    logId = dto.log_id,
                    createdAt = dto.created_at ?: "",
                    updatedAt = dto.updated_at ?: "",
                    deletedAt = dto.deleted_at
                )
            }
            orderDao.insertOrderItems(itemEntities)
        }
    }
    
    private suspend fun syncPayments(companyId: Int) {
        val response = apiService.getTransactions(companyId)
        val entities = response.data.map { dto ->
            TransactionEntity(
                id = dto.id,
                date = dto.date,
                amount = dto.amount,
                type = dto.type,
                debitCode = dto.debitCode,
                creditCode = dto.creditCode,
                comment = dto.comment,
                companyId = companyId,
                partyName = dto.partyName,
                isReceived = dto.isReceived
            )
        }
        transactionDao.insertTransactions(entities)
    }
    
    private suspend fun syncPriceLists(companyId: Int) {
        val response = apiService.getPriceLists(companyId)
        val entities = response.data.map { dto ->
            PriceListEntity(
                id = dto.id,
                name = dto.name,
                itemsCount = dto.itemsCount,
                companyId = companyId
            )
        }
        priceListDao.insertPriceLists(entities)
    }
    
    override suspend fun syncMasterData(companyId: Int): Result<Unit> {
        return sync(companyId, listOf(SyncType.AllMasterData))
    }
    
    override suspend fun fullSync(companyId: Int): Result<Unit> {
        return try {
            val lastSync = syncDao.getSyncTimestamp("master_data")
            val timestamp = lastSync?.timestamp ?: "1970-01-01 00:00:00"
            
            val response = apiService.syncMasterData(companyId, timestamp)
            
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
                            companyId = dto.company_id,
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
                            companyId = dto.company_id,
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
                        lastSyncedAt = com.sales.app.util.TimeProvider.now().toEpochMilliseconds()
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
