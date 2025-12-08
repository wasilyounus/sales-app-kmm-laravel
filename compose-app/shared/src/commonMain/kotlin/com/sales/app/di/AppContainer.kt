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
import com.sales.app.presentation.inventory.*
import com.sales.app.presentation.settings.AccountSettingsViewModel
import com.sales.app.presentation.login.LoginViewModel
import com.sales.app.presentation.register.RegisterViewModel
import com.sales.app.presentation.home.HomeViewModel
import com.sales.app.presentation.items.*
import com.sales.app.presentation.parties.*
import com.sales.app.presentation.sync.SyncViewModel
import com.sales.app.domain.repository.*
import com.sales.app.presentation.quotes.*
import com.sales.app.presentation.purchases.*
import com.sales.app.presentation.sales.*
import com.sales.app.presentation.orders.*
import com.sales.app.presentation.payments.*
import com.sales.app.presentation.pricelists.*
import com.sales.app.presentation.deliverynotes.*
import com.sales.app.presentation.grns.*

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
    // Repositories
    val authRepository: AuthRepository = AuthRepositoryImpl(apiService, database.userDao(), dataStore)
    val itemRepository: ItemRepository = ItemRepositoryImpl(apiService, database.itemDao(), database.uqcDao())
    val partyRepository: PartyRepository = PartyRepositoryImpl(apiService, database.partyDao(), database.addressDao())
    val syncRepository: SyncRepository = SyncRepositoryImpl(
        apiService,
        database.itemDao(),
        database.partyDao(),
        database.addressDao(),
        database.taxDao(),
        database.uqcDao(),
        database.saleDao(),
        database.quoteDao(),
        database.quoteItemDao(),
        database.purchaseDao(),
        database.orderDao(),
        database.orderItemDao(),
        database.transactionDao(),
        database.priceListDao(),
        database.syncDao()
    )
    val quoteRepository: QuoteRepository = QuoteRepositoryImpl(apiService, database.quoteDao(), database.quoteItemDao())
    val saleRepository: SaleRepository = SaleRepositoryImpl(apiService, database.saleDao(), database.saleItemDao())
    val orderRepository: OrderRepository = OrderRepositoryImpl(apiService, database.orderDao(), database.orderItemDao())
    val purchaseRepository: PurchaseRepository = PurchaseRepositoryImpl(apiService, database.purchaseDao(), database.purchaseItemDao())
    val accountRepository: AccountRepository = AccountRepositoryImpl(apiService, database.accountDao())
    val inventoryRepository: InventoryRepository = InventoryRepositoryImpl(database.inventoryDao(), database.itemDao())
    val taxRepository: TaxRepository = TaxRepositoryImpl(apiService, database.taxDao())
    val paymentRepository: PaymentRepository = PaymentRepositoryImpl(apiService, database.transactionDao())
    val priceListRepository: PriceListRepository = PriceListRepositoryImpl(apiService, database.priceListDao())
    val deliveryNoteRepository: DeliveryNoteRepository = DeliveryNoteRepositoryImpl(apiService, database.deliveryNoteDao())
    val grnRepository: GrnRepository = GrnRepositoryImpl(apiService, database.grnDao())

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
    
    val syncDataUseCase = SyncDataUseCase(syncRepository)
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
        getQuotesUseCase,
        getAccountUseCase
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
    fun createPartyFormViewModel() = PartyFormViewModel(
        createPartyUseCase,
        updatePartyUseCase,
        getPartyByIdUseCase
    )
    fun createQuotesViewModel() = QuotesViewModel(
        getQuotesUseCase,
        getPartiesUseCase,
        syncQuotesUseCase
    )
    fun createQuoteFormViewModel() = QuoteFormViewModel(
        createQuoteUseCase,
        updateQuoteUseCase,
        getQuoteByIdUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createQuoteViewViewModel() = QuoteViewViewModel(
        getQuoteByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )
    
    // Sales ViewModels
    fun createSalesViewModel() = SalesViewModel(
        getSalesUseCase,
        syncSalesUseCase
    )
    fun createSaleFormViewModel() = SaleFormViewModel(
        createSaleUseCase,
        getSaleByIdUseCase,
        updateSaleUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createSaleViewViewModel() = SaleViewViewModel(
        getSaleByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )
    
    // Orders ViewModels
    fun createOrdersViewModel() = OrdersViewModel(
        getOrdersUseCase,
        syncOrdersUseCase
    )
    fun createOrderFormViewModel() = OrderFormViewModel(
        createOrderUseCase,
        getOrderByIdUseCase,
        updateOrderUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createOrderViewViewModel() = OrderViewViewModel(
        getOrderByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )

    // Purchases ViewModels
    fun createPurchasesViewModel() = PurchasesViewModel(
        getPurchasesUseCase,
        syncPurchasesUseCase
    )
    fun createPurchaseFormViewModel() = PurchaseFormViewModel(
        createPurchaseUseCase,
        getPurchaseByIdUseCase,
        updatePurchaseUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createPurchaseViewViewModel() = PurchaseViewViewModel(
        getPurchaseByIdUseCase,
        getPartyByIdUseCase,
        getItemsUseCase
    )

    fun createAccountSettingsViewModel() = AccountSettingsViewModel(
        getAccountUseCase,
        updateAccountUseCase,
        fetchAccountUseCase,
        getTaxesUseCase
    )

    fun createSyncViewModel() = SyncViewModel(syncDataUseCase, syncMasterDataUseCase, fullSyncUseCase)
    
    // Inventory ViewModels
    fun createInventoryViewModel() = InventoryViewModel(getInventorySummaryUseCase, syncMasterDataUseCase)
    fun createStockAdjustmentViewModel() = StockAdjustmentViewModel(adjustStockUseCase, getItemsUseCase)

    // Payments ViewModels
    fun createPaymentsViewModel() = PaymentsViewModel(paymentRepository)
    fun createPaymentFormViewModel() = PaymentFormViewModel(paymentRepository, partyRepository)

    // Price Lists ViewModels
    fun createPriceListsViewModel() = PriceListsViewModel(priceListRepository)
    fun createPriceListDetailViewModel() = PriceListDetailViewModel(priceListRepository, itemRepository)

    // Delivery Notes
    val getDeliveryNotesUseCase = GetDeliveryNotesUseCase(deliveryNoteRepository)
    val syncDeliveryNotesUseCase = SyncDeliveryNotesUseCase(deliveryNoteRepository)
    val createDeliveryNoteUseCase = CreateDeliveryNoteUseCase(deliveryNoteRepository)
    
    fun createDeliveryNotesViewModel() = DeliveryNotesViewModel(getDeliveryNotesUseCase, syncDeliveryNotesUseCase)

    // GRNs
    val getGrnsUseCase = GetGrnsUseCase(grnRepository)
    val syncGrnsUseCase = SyncGrnsUseCase(grnRepository)
    val createGrnUseCase = CreateGrnUseCase(grnRepository)
    
    fun createGrnsViewModel() = GrnsViewModel(getGrnsUseCase, syncGrnsUseCase)
}
