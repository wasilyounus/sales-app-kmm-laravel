package com.sales.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel

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
import com.sales.app.presentation.settings.AccountSettingsScreen
import com.sales.app.presentation.deliverynotes.DeliveryNotesScreen
import com.sales.app.presentation.grns.GrnsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: Any,
    appContainer: SalesAppContainer
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
                accountId = 1, // TODO: Get from auth
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
                onNavigateToAccountSettings = { navController.navigate(AccountSettings) },
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
                accountId = 1,
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
                accountId = 1,
                itemId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<ItemEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<ItemEdit>()
            ItemFormScreen(
                viewModel = viewModel { appContainer.createItemFormViewModel() },
                accountId = 1,
                itemId = route.itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Parties
        composable<Parties> {
            PartiesScreen(
                viewModel = viewModel { appContainer.createPartiesViewModel() },
                accountId = 1,
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
                accountId = 1,
                partyId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<PartyEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<PartyEdit>()
            PartyFormScreen(
                viewModel = viewModel { appContainer.createPartyFormViewModel() },
                accountId = 1,
                partyId = route.partyId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Quotes
        composable<Quotes> {
            QuotesScreen(
                viewModel = viewModel { appContainer.createQuotesViewModel() },
                accountId = 1,
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
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<QuoteEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<QuoteEdit>()
            QuoteFormScreen(
                viewModel = viewModel { appContainer.createQuoteFormViewModel() },
                accountId = 1,
                quoteId = route.quoteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<QuoteDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<QuoteDetail>()
            QuoteViewScreen(
                viewModel = viewModel { appContainer.createQuoteViewViewModel() },
                accountId = 1,
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
                accountId = 1,
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
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<SaleEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<SaleEdit>()
            SaleFormScreen(
                viewModel = viewModel { appContainer.createSaleFormViewModel() },
                accountId = 1,
                saleId = route.saleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SaleDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<SaleDetail>()
            com.sales.app.presentation.sales.SaleViewScreen(
                viewModel = viewModel { appContainer.createSaleViewViewModel() },
                accountId = 1,
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
                accountId = 1,
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
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<OrderEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<OrderEdit>()
            OrderFormScreen(
                viewModel = viewModel { appContainer.createOrderFormViewModel() },
                accountId = 1,
                orderId = route.orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<OrderDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<OrderDetail>()
            com.sales.app.presentation.orders.OrderViewScreen(
                viewModel = viewModel { appContainer.createOrderViewViewModel() },
                accountId = 1,
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
                accountId = 1,
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
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<PurchaseEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<PurchaseEdit>()
            PurchaseFormScreen(
                viewModel = viewModel { appContainer.createPurchaseFormViewModel() },
                accountId = 1,
                purchaseId = route.purchaseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<PurchaseDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<PurchaseDetail>()
            com.sales.app.presentation.purchases.PurchaseViewScreen(
                viewModel = viewModel { appContainer.createPurchaseViewViewModel() },
                accountId = 1,
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
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Settings & Sync
        composable<Settings> {
            SyncScreen(
                viewModel = viewModel { appContainer.createSyncViewModel() },
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<AccountSettings> {
            AccountSettingsScreen(
                viewModel = viewModel { appContainer.createAccountSettingsViewModel() },
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Payments
        composable<Payments> {
            com.sales.app.presentation.payments.PaymentsScreen(
                viewModel = viewModel { appContainer.createPaymentsViewModel() },
                accountId = 1,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreatePayment = {
                    navController.navigate(PaymentCreate)
                }
            )
        }

        composable<PaymentCreate> {
            com.sales.app.presentation.payments.PaymentFormScreen(
                viewModel = viewModel { appContainer.createPaymentFormViewModel() },
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Price Lists
        composable<PriceLists> {
            com.sales.app.presentation.pricelists.PriceListsScreen(
                viewModel = viewModel { appContainer.createPriceListsViewModel() },
                accountId = 1,
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
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Delivery Notes
        composable<DeliveryNotes> {
            DeliveryNotesScreen(
                viewModel = viewModel { appContainer.createDeliveryNotesViewModel() },
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // GRNs
        composable<Grns> {
            GrnsScreen(
                viewModel = viewModel { appContainer.createGrnsViewModel() },
                accountId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
