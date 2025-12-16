package com.sales.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.sales.app.di.SalesAppContainer
import com.sales.app.presentation.login.LoginScreen
import com.sales.app.presentation.register.RegisterScreen
import com.sales.app.presentation.home.HomeScreen
import com.sales.app.presentation.items.ItemsScreen
import com.sales.app.presentation.items.ItemFormScreen
import com.sales.app.presentation.parties.PartiesScreen
import com.sales.app.presentation.parties.PartyFormScreen
import com.sales.app.presentation.quotes.QuotesScreen
import com.sales.app.presentation.quotes.QuoteFormScreen
import com.sales.app.presentation.quotes.QuoteViewScreen
import com.sales.app.presentation.sales.SalesScreen
import com.sales.app.presentation.sales.SaleFormScreen
import com.sales.app.presentation.orders.OrdersScreen
import com.sales.app.presentation.orders.OrderFormScreen
import com.sales.app.presentation.purchases.PurchasesScreen
import com.sales.app.presentation.purchases.PurchaseFormScreen
import com.sales.app.presentation.inventory.InventoryScreen
import com.sales.app.presentation.sync.SyncScreen
import com.sales.app.presentation.settings.CompanySettingsScreen
import com.sales.app.presentation.deliverynotes.DeliveryNotesScreen
import com.sales.app.presentation.grns.GrnsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: Any,
    appContainer: SalesAppContainer
) {
    val mainViewModel: com.sales.app.presentation.main.MainViewModel = viewModel { appContainer.createMainViewModel() }
    val companyId by mainViewModel.selectedCompanyId.collectAsState()
    val companies by mainViewModel.companies.collectAsState()
    val showCompanySelectionDialog by mainViewModel.showCompanySelectionDialog.collectAsState()
    val currentUser by mainViewModel.currentUser.collectAsState()

    androidx.compose.runtime.LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    
    if (showCompanySelectionDialog) {
        com.sales.app.presentation.components.CompanySelectionDialog(
            companies = companies,
            onCompanySelected = { mainViewModel.selectCompany(it) }
        )
    }
    
    val drawerState = androidx.compose.material3.rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    androidx.compose.material3.ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
             androidx.compose.material3.ModalDrawerSheet {
                 androidx.compose.material3.Text(
                     "Menu",
                     modifier = androidx.compose.ui.Modifier.padding(16.dp),
                     style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                 )
             }
        },
        gesturesEnabled = true // Can be conditional based on route
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // Auth
            composable<Login> {
                LoginScreen(
                    viewModel = viewModel { appContainer.createLoginViewModel() },
                    onNavigateToRegister = {
                        navController.navigate(Register)
                    },
                    onNavigateToHome = {
                        navController.navigate(Home) {
                            popUpTo<Login> { inclusive = true }
                        }
                    }
                )
            }
            
            composable<Register> {
                RegisterScreen(
                    viewModel = viewModel { appContainer.createRegisterViewModel() },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        navController.navigate(Home) {
                            popUpTo<Login> { inclusive = true }
                        }
                    }
                )
            }
            
            composable<Home> {
                HomeScreen(
                    viewModel = viewModel { appContainer.createHomeViewModel() },
                    companyId = companyId,
                    companies = companies,
                    onSelectCompany = { mainViewModel.selectCompany(it) },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToLogin = {
                        navController.navigate(Login) {
                            popUpTo(Login) { inclusive = true }
                        }
                    },
                    onNavigateToItems = { navController.navigate(Items) },
                    onNavigateToParties = { navController.navigate(Parties) },
                    onNavigateToQuotes = { navController.navigate(Quotes) },
                    onNavigateToOrders = { navController.navigate(Orders) },
                    onNavigateToSales = { navController.navigate(Sales) },
                    onNavigateToPurchases = { navController.navigate(Purchases) },
                    onNavigateToInventory = { navController.navigate(Inventory) },
                    onNavigateToSync = { navController.navigate(Settings) },
                    onNavigateToTransfers = { /* TODO: Implement transfers screen */ },
                    onNavigateToCompanySettings = { navController.navigate(CompanySettings) },
                    onNavigateToPayments = { navController.navigate(Payments) },
                    onNavigateToPriceLists = { navController.navigate(PriceLists) },
                    onNavigateToDeliveryNotes = { navController.navigate(DeliveryNotes) },
                    onNavigateToGrns = { navController.navigate(Grns) }
                )
            }
            
            // Items
            composable<Items> {
                ItemsScreen(
                    viewModel = viewModel { appContainer.createItemsViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemEdit = { itemId ->
                        navController.navigate(ItemEdit(itemId))
                    },
                    onNavigateToCreateItem = {
                        navController.navigate(ItemCreate)
                    }
                )
            }
            
            composable<ItemCreate> {
                ItemFormScreen(
                    viewModel = viewModel { appContainer.createItemFormViewModel() },
                    companyId = companyId,
                    itemId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<ItemEdit> { backStackEntry ->
                val route = backStackEntry.toRoute<ItemEdit>()
                ItemFormScreen(
                    viewModel = viewModel { appContainer.createItemFormViewModel() },
                    companyId = companyId,
                    itemId = route.itemId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Parties
            composable<Parties> {
                PartiesScreen(
                    viewModel = viewModel { appContainer.createPartiesViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPartyEdit = { partyId ->
                        navController.navigate(PartyEdit(partyId))
                    },
                    onNavigateToCreateParty = {
                        navController.navigate(PartyCreate)
                    }
                )
            }
            
            composable<PartyCreate> {
                PartyFormScreen(
                    viewModel = viewModel { appContainer.createPartyFormViewModel() },
                    companyId = companyId,
                    partyId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<PartyEdit> { backStackEntry ->
                val route = backStackEntry.toRoute<PartyEdit>()
                PartyFormScreen(
                    viewModel = viewModel { appContainer.createPartyFormViewModel() },
                    companyId = companyId,
                    partyId = route.partyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Quotes
            composable<Quotes> {
                QuotesScreen(
                    viewModel = viewModel { appContainer.createQuotesViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreateQuote = {
                        navController.navigate(QuoteCreate)
                    },
                    onNavigateToQuoteDetails = { quoteId ->
                        navController.navigate(QuoteDetail(quoteId))
                    }
                )
            }
            
            composable<QuoteCreate> {
                QuoteFormScreen(
                    viewModel = viewModel { appContainer.createQuoteFormViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<QuoteEdit> { backStackEntry ->
                val route = backStackEntry.toRoute<QuoteEdit>()
                QuoteFormScreen(
                    viewModel = viewModel { appContainer.createQuoteFormViewModel() },
                    companyId = companyId,
                    quoteId = route.quoteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<QuoteDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<QuoteDetail>()
                QuoteViewScreen(
                    viewModel = viewModel { appContainer.createQuoteViewViewModel() },
                    companyId = companyId,
                    quoteId = route.quoteId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(QuoteEdit(id))
                    }
                )
            }
            
            // Sales
            composable<Sales> {
                SalesScreen(
                    viewModel = viewModel { appContainer.createSalesViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreateSale = {
                        navController.navigate(SaleCreate)
                    },
                    onNavigateToSaleDetails = { saleId ->
                        navController.navigate(SaleDetail(saleId))
                    }
                )
            }
            
            composable<SaleCreate> {
                SaleFormScreen(
                    viewModel = viewModel { appContainer.createSaleFormViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<SaleEdit> { backStackEntry ->
                val route = backStackEntry.toRoute<SaleEdit>()
                SaleFormScreen(
                    viewModel = viewModel { appContainer.createSaleFormViewModel() },
                    companyId = companyId,
                    saleId = route.saleId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            composable<SaleDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<SaleDetail>()
                com.sales.app.presentation.sales.SaleViewScreen(
                    viewModel = viewModel { appContainer.createSaleViewViewModel() },
                    companyId = companyId,
                    saleId = route.saleId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(SaleEdit(id))
                    }
                )
            }
    
            // Orders
            composable<Orders> {
                OrdersScreen(
                    viewModel = viewModel { appContainer.createOrdersViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreateOrder = {
                        navController.navigate(OrderCreate)
                    },
                    onNavigateToOrderDetails = { orderId ->
                        navController.navigate(OrderDetail(orderId))
                    }
                )
            }
            
            composable<OrderCreate> {
                OrderFormScreen(
                    viewModel = viewModel { appContainer.createOrderFormViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<OrderEdit> { backStackEntry ->
                val route = backStackEntry.toRoute<OrderEdit>()
                OrderFormScreen(
                    viewModel = viewModel { appContainer.createOrderFormViewModel() },
                    companyId = companyId,
                    orderId = route.orderId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            composable<OrderDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<OrderDetail>()
                com.sales.app.presentation.orders.OrderViewScreen(
                    viewModel = viewModel { appContainer.createOrderViewViewModel() },
                    companyId = companyId,
                    orderId = route.orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(OrderEdit(id))
                    }
                )
            }
    
            // Purchases
            composable<Purchases> {
                PurchasesScreen(
                    viewModel = viewModel { appContainer.createPurchasesViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreatePurchase = {
                        navController.navigate(PurchaseCreate)
                    },
                    onNavigateToPurchaseDetails = { purchaseId ->
                        navController.navigate(PurchaseDetail(purchaseId))
                    }
                )
            }
            
            composable<PurchaseCreate> {
                PurchaseFormScreen(
                    viewModel = viewModel { appContainer.createPurchaseFormViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<PurchaseEdit> { backStackEntry ->
                val route = backStackEntry.toRoute<PurchaseEdit>()
                PurchaseFormScreen(
                    viewModel = viewModel { appContainer.createPurchaseFormViewModel() },
                    companyId = companyId,
                    purchaseId = route.purchaseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            composable<PurchaseDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<PurchaseDetail>()
                com.sales.app.presentation.purchases.PurchaseViewScreen(
                    viewModel = viewModel { appContainer.createPurchaseViewViewModel() },
                    companyId = companyId,
                    purchaseId = route.purchaseId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(PurchaseEdit(id))
                    }
                )
            }
    
            // Inventory
            composable<Inventory> {
                InventoryScreen(
                    viewModel = viewModel { appContainer.createInventoryViewModel() },
                    stockAdjustmentViewModel = viewModel { appContainer.createStockAdjustmentViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Settings & Sync
            composable<Settings> {
                SyncScreen(
                    viewModel = viewModel { appContainer.createSyncViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<CompanySettings> {
                CompanySettingsScreen(
                    viewModel = viewModel { appContainer.createCompanySettingsViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            // Payments
            composable<Payments> {
                com.sales.app.presentation.payments.PaymentsScreen(
                    viewModel = viewModel { appContainer.createPaymentsViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreatePayment = {
                        navController.navigate(PaymentCreate)
                    }
                )
            }
    
            composable<PaymentCreate> {
                com.sales.app.presentation.payments.PaymentFormScreen(
                    viewModel = viewModel { appContainer.createPaymentFormViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            // Price Lists
            composable<PriceLists> {
                com.sales.app.presentation.pricelists.PriceListsScreen(
                    viewModel = viewModel { appContainer.createPriceListsViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id ->
                        navController.navigate(PriceListDetail(id))
                    }
                )
            }
    
            composable<PriceListDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<PriceListDetail>()
                com.sales.app.presentation.pricelists.PriceListDetailScreen(
                    viewModel = viewModel { appContainer.createPriceListDetailViewModel() },
                    priceListId = route.priceListId,
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            // Delivery Notes
            composable<DeliveryNotes> {
                DeliveryNotesScreen(
                    viewModel = viewModel { appContainer.createDeliveryNotesViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
    
            // GRNs
            composable<Grns> {
                GrnsScreen(
                    viewModel = viewModel { appContainer.createGrnsViewModel() },
                    companyId = companyId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
