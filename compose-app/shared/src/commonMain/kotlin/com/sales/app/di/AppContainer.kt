package com.sales.app.di

import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sales.app.data.local.AppDatabase
import com.sales.app.data.local.AccountPreferences
import com.sales.app.data.remote.ApiService
import com.sales.app.data.repository.*
import com.sales.app.domain.usecase.*
import com.sales.app.presentation.inventory.InventoryViewModel
import com.sales.app.presentation.inventory.StockAdjustmentViewModel
import com.sales.app.presentation.login.LoginViewModel
import com.sales.app.presentation.register.RegisterViewModel
import com.sales.app.presentation.home.HomeViewModel
import com.sales.app.presentation.items.ItemsViewModel
import com.sales.app.presentation.items.ItemFormViewModel
import com.sales.app.presentation.parties.PartiesViewModel
import com.sales.app.presentation.sync.SyncViewModel

class SalesAppContainer(
    private val database: AppDatabase,
    private val httpClient: HttpClient,
    private val dataStore: DataStore<Preferences>
) {
    // Services
    private val apiService = ApiService(httpClient) {
        // Token Provider
        dataStore.data.map { preferences ->
            preferences[androidx.datastore.preferences.core.stringPreferencesKey("auth_token")]
        }.firstOrNull()
    }
    
    private val accountPreferences = AccountPreferences(dataStore)
    
    // Repositories
    val authRepository = AuthRepository(apiService, database.userDao(), dataStore)
    val itemRepository = ItemRepository(apiService, database.itemDao())
    val partyRepository = PartyRepository(apiService, database.partyDao(), database.addressDao())
    val syncRepository = SyncRepository(apiService, database.itemDao(), database.partyDao(), database.taxDao(), database.uqcDao(), database.syncDao())
    val quoteRepository = QuoteRepository(apiService, database.quoteDao(), database.quoteItemDao())
    val saleRepository = SaleRepository(apiService, database.saleDao(), database.saleItemDao())
    val orderRepository = OrderRepository(apiService, database.orderDao(), database.orderItemDao())
    val purchaseRepository = PurchaseRepository(apiService, database.purchaseDao(), database.purchaseItemDao())
    val accountRepository = com.sales.app.data.repository.AccountRepositoryImpl(apiService, database.accountDao())
    val inventoryRepository = InventoryRepository(database.inventoryDao(), database.itemDao())
    val taxRepository = TaxRepository(apiService, database.taxDao())

    // Use Cases
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val getItemsUseCase = GetItemsUseCase(itemRepository)
    val searchItemsUseCase = SearchItemsUseCase(itemRepository)
    val createItemUseCase = CreateItemUseCase(itemRepository)
    val updateItemUseCase = UpdateItemUseCase(itemRepository)
    val getItemByIdUseCase = GetItemByIdUseCase(itemRepository)
    val getUqcsUseCase = GetUqcsUseCase(itemRepository)
    
    val getPartiesUseCase = GetPartiesUseCase(partyRepository)
    val searchPartiesUseCase = SearchPartiesUseCase(partyRepository)
    val createPartyUseCase = CreatePartyUseCase(partyRepository)
    val updatePartyUseCase = UpdatePartyUseCase(partyRepository)
    val getPartyByIdUseCase = GetPartyByIdUseCase(partyRepository)
    
    val getQuotesUseCase = GetQuotesUseCase(quoteRepository)
    val getQuoteByIdUseCase = GetQuoteByIdUseCase(quoteRepository)
    val createQuoteUseCase = CreateQuoteUseCase(quoteRepository)
    val updateQuoteUseCase = UpdateQuoteUseCase(quoteRepository)
    val deleteQuoteUseCase = DeleteQuoteUseCase(quoteRepository)
    val syncQuotesUseCase = SyncQuotesUseCase(quoteRepository)
    
    // Sales Use Cases
    val getSalesUseCase = GetSalesUseCase(saleRepository)
    val getSaleByIdUseCase = GetSaleByIdUseCase(saleRepository)
    val createSaleUseCase = CreateSaleUseCase(saleRepository)
    val updateSaleUseCase = UpdateSaleUseCase(saleRepository)
    val deleteSaleUseCase = DeleteSaleUseCase(saleRepository)
    val syncSalesUseCase = SyncSalesUseCase(saleRepository)
    
    // Orders Use Cases
    val getOrdersUseCase = GetOrdersUseCase(orderRepository)
    val getOrderByIdUseCase = GetOrderByIdUseCase(orderRepository)
    val createOrderUseCase = CreateOrderUseCase(orderRepository)
    val updateOrderUseCase = UpdateOrderUseCase(orderRepository)
    val deleteOrderUseCase = DeleteOrderUseCase(orderRepository)
    val syncOrdersUseCase = SyncOrdersUseCase(orderRepository)
    
    // Purchases Use Cases
    val getPurchasesUseCase = GetPurchasesUseCase(purchaseRepository)
    val getPurchaseByIdUseCase = GetPurchaseByIdUseCase(purchaseRepository)
    val createPurchaseUseCase = CreatePurchaseUseCase(purchaseRepository)
    val updatePurchaseUseCase = UpdatePurchaseUseCase(purchaseRepository)
    val deletePurchaseUseCase = DeletePurchaseUseCase(purchaseRepository)
    val syncPurchasesUseCase = SyncPurchasesUseCase(purchaseRepository)
    
    val getAccountUseCase = GetAccountUseCase(accountRepository)
    val updateAccountUseCase = UpdateAccountUseCase(accountRepository)
    val fetchAccountUseCase = FetchAccountUseCase(accountRepository)
    
    val syncMasterDataUseCase = SyncMasterDataUseCase(syncRepository)
    val fullSyncUseCase = FullSyncUseCase(syncRepository)
    val logoutUseCase = LogoutUseCase(authRepository)

    val getInventorySummaryUseCase = GetInventorySummaryUseCase(inventoryRepository)
    val adjustStockUseCase = AdjustStockUseCase(inventoryRepository)
    val getStockMovementsUseCase = GetStockMovementsUseCase(inventoryRepository)
    val getTaxesUseCase = GetTaxesUseCase(taxRepository)
    
    // ViewModel Factories
    fun createLoginViewModel() = LoginViewModel(loginUseCase, accountPreferences, apiService)
    fun createRegisterViewModel() = RegisterViewModel(registerUseCase)
    fun createHomeViewModel() = HomeViewModel(
        logoutUseCase,
        getItemsUseCase,
        getPartiesUseCase,
        getQuotesUseCase
    )
    fun createItemsViewModel() = ItemsViewModel(getItemsUseCase, syncMasterDataUseCase, getUqcsUseCase)
    fun createItemFormViewModel() = ItemFormViewModel(
        createItemUseCase,
        updateItemUseCase,
        getItemByIdUseCase,
        getUqcsUseCase,
        getTaxesUseCase,
        accountRepository
    )
    fun createPartiesViewModel() = PartiesViewModel(getPartiesUseCase, searchPartiesUseCase)
    fun createPartyFormViewModel() = com.sales.app.presentation.parties.PartyFormViewModel(
        createPartyUseCase,
        updatePartyUseCase,
        getPartyByIdUseCase
    )
    fun createQuotesViewModel() = com.sales.app.presentation.quotes.QuotesViewModel(
        getQuotesUseCase,
        getPartiesUseCase,
        syncQuotesUseCase
    )
    fun createQuoteFormViewModel() = com.sales.app.presentation.quotes.QuoteFormViewModel(
        createQuoteUseCase,
        updateQuoteUseCase,
        getQuoteByIdUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createQuoteViewViewModel() = com.sales.app.presentation.quotes.QuoteViewViewModel(
        getQuoteByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )
    
    // Sales ViewModels
    fun createSalesViewModel() = com.sales.app.presentation.sales.SalesViewModel(
        getSalesUseCase,
        syncSalesUseCase
    )
    fun createSaleFormViewModel() = com.sales.app.presentation.sales.SaleFormViewModel(
        createSaleUseCase,
        getSaleByIdUseCase,
        updateSaleUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createSaleViewViewModel() = com.sales.app.presentation.sales.SaleViewViewModel(
        getSaleByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )
    
    // Orders ViewModels
    fun createOrdersViewModel() = com.sales.app.presentation.orders.OrdersViewModel(
        getOrdersUseCase,
        syncOrdersUseCase
    )
    fun createOrderFormViewModel() = com.sales.app.presentation.orders.OrderFormViewModel(
        createOrderUseCase,
        getOrderByIdUseCase,
        updateOrderUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createOrderViewViewModel() = com.sales.app.presentation.orders.OrderViewViewModel(
        getOrderByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )

    // Purchases ViewModels
    fun createPurchasesViewModel() = com.sales.app.presentation.purchases.PurchasesViewModel(
        getPurchasesUseCase,
        syncPurchasesUseCase
    )
    fun createPurchaseFormViewModel() = com.sales.app.presentation.purchases.PurchaseFormViewModel(
        createPurchaseUseCase,
        getPurchaseByIdUseCase,
        updatePurchaseUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createPurchaseViewViewModel() = com.sales.app.presentation.purchases.PurchaseViewViewModel(
        getPurchaseByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )

    fun createAccountSettingsViewModel() = com.sales.app.presentation.settings.AccountSettingsViewModel(
        getAccountUseCase,
        updateAccountUseCase,
        fetchAccountUseCase
    )

    fun createSyncViewModel() = SyncViewModel(syncMasterDataUseCase, fullSyncUseCase)
    
    // Inventory ViewModels
    fun createInventoryViewModel() = InventoryViewModel(getInventorySummaryUseCase)
    fun createStockAdjustmentViewModel() = StockAdjustmentViewModel(adjustStockUseCase, getItemsUseCase)
}
