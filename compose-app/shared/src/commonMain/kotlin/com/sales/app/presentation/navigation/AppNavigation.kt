package com.sales.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
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

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    appContainer: SalesAppContainer
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel { appContainer.createLoginViewModel() },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel { appContainer.createRegisterViewModel() },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel { appContainer.createHomeViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationRoute ?: Screen.Login.route) { 
                            inclusive = true 
                        }
                    }
                },
                onNavigateToItems = {
                    navController.navigate(Screen.Items.route)
                },
                onNavigateToParties = {
                    navController.navigate(Screen.Parties.route)
                },
                onNavigateToQuotes = {
                    navController.navigate(Screen.Quotes.route)
                },
                onNavigateToPayments = {
                    navController.navigate(Screen.Payments.route)
                },
                onNavigateToPriceLists = {
                    navController.navigate(Screen.PriceLists.route)
                },
                onNavigateToSync = {
                    navController.navigate(Screen.Settings.route) // Using Settings route for Sync
                },
                onNavigateToInventory = {
                    navController.navigate(Screen.Inventory.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToSales = {
                    navController.navigate(Screen.Sales.route)
                },
                onNavigateToPurchases = {
                    navController.navigate(Screen.Purchases.route)
                },
                onNavigateToTransfers = {
                     // StockAdjustment is now a dialog in Inventory, so we navigate to Inventory if we want to show it.
                     // Or we could pass a flag to auto-open it. For now, just go to Inventory.
                    navController.navigate(Screen.Inventory.route)
                },
                onNavigateToAccountSettings = {
                    navController.navigate(Screen.AccountSettings.route)
                }
            )
        }
        
        
        composable(Screen.Items.route) {
            ItemsScreen(
                viewModel = viewModel { appContainer.createItemsViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToItemEdit = { itemId ->
                    navController.navigate(Screen.ItemEdit.createRoute(itemId))
                },
                onNavigateToCreateItem = {
                    navController.navigate(Screen.ItemCreate.route)
                }
            )
        }
        

        
        composable(Screen.ItemCreate.route) {
            ItemFormScreen(
                viewModel = viewModel { appContainer.createItemFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ItemEdit.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId")
            ItemFormScreen(
                viewModel = viewModel { appContainer.createItemFormViewModel() },
                accountId = 1, // TODO: Get from auth
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Parties.route) {
            PartiesScreen(
                viewModel = viewModel { appContainer.createPartiesViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPartyEdit = { partyId ->
                    navController.navigate(Screen.PartyEdit.createRoute(partyId))
                },
                onNavigateToCreateParty = {
                    navController.navigate(Screen.PartyCreate.route)
                }
            )
        }
        
        composable(Screen.PartyCreate.route) {
            PartyFormScreen(
                viewModel = viewModel { appContainer.createPartyFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PartyEdit.route,
            arguments = listOf(navArgument("partyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val partyId = backStackEntry.arguments?.getInt("partyId")
            PartyFormScreen(
                viewModel = viewModel { appContainer.createPartyFormViewModel() },
                accountId = 1, // TODO: Get from auth
                partyId = partyId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        
        // Placeholders for other modules
        composable(Screen.Quotes.route) {
            QuotesScreen(
                viewModel = viewModel { appContainer.createQuotesViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateQuote = {
                    navController.navigate(Screen.QuoteCreate.route)
                },
                onNavigateToQuoteDetails = { quoteId ->
                    navController.navigate(Screen.QuoteDetail.createRoute(quoteId))
                }
            )
        }
        
        composable(Screen.QuoteCreate.route) {
            QuoteFormScreen(
                viewModel = viewModel { appContainer.createQuoteFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.QuoteEdit.route,
            arguments = listOf(navArgument("quoteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val quoteId = backStackEntry.arguments?.getInt("quoteId")
            QuoteFormScreen(
                viewModel = viewModel { appContainer.createQuoteFormViewModel() },
                accountId = 1, // TODO: Get from auth
                quoteId = quoteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.QuoteDetail.route,
            arguments = listOf(navArgument("quoteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val quoteId = backStackEntry.arguments?.getInt("quoteId") ?: return@composable
            com.sales.app.presentation.quotes.QuoteViewScreen(
                viewModel = viewModel { appContainer.createQuoteViewViewModel() },
                accountId = 1, // TODO: Get from auth
                quoteId = quoteId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.QuoteEdit.createRoute(id))
                }
            )
        }
        
        composable(Screen.Sales.route) {
            com.sales.app.presentation.sales.SalesScreen(
                viewModel = viewModel { appContainer.createSalesViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateSale = {
                    navController.navigate(Screen.SaleCreate.route)
                },
                onNavigateToSaleDetails = { saleId ->
                    navController.navigate(Screen.SaleDetail.createRoute(saleId))
                }
            )
        }
        
        composable(Screen.SaleCreate.route) {
            com.sales.app.presentation.sales.SaleFormScreen(
                viewModel = viewModel { appContainer.createSaleFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.SaleEdit.route,
            arguments = listOf(navArgument("saleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getInt("saleId")
            com.sales.app.presentation.sales.SaleFormScreen(
                viewModel = viewModel { appContainer.createSaleFormViewModel() },
                accountId = 1, // TODO: Get from auth
                saleId = saleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SaleDetail.route,
            arguments = listOf(navArgument("saleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getInt("saleId") ?: return@composable
            // Placeholder for SaleViewScreen, redirecting to Edit for now or just showing text
            // For now, let's redirect to Edit as View is not implemented yet
            com.sales.app.presentation.sales.SaleViewScreen(
                viewModel = viewModel { appContainer.createSaleViewViewModel() },
                accountId = 1, // TODO: Get from auth
                saleId = saleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.SaleEdit.createRoute(id))
                }
            )
        }

        composable(Screen.Orders.route) {
            com.sales.app.presentation.orders.OrdersScreen(
                viewModel = viewModel { appContainer.createOrdersViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateOrder = {
                    navController.navigate(Screen.OrderCreate.route)
                },
                onNavigateToOrderDetails = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            )
        }
        
        composable(Screen.OrderCreate.route) {
            com.sales.app.presentation.orders.OrderFormScreen(
                viewModel = viewModel { appContainer.createOrderFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.OrderEdit.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId")
            com.sales.app.presentation.orders.OrderFormScreen(
                viewModel = viewModel { appContainer.createOrderFormViewModel() },
                accountId = 1, // TODO: Get from auth
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
             com.sales.app.presentation.orders.OrderViewScreen(
                viewModel = viewModel { appContainer.createOrderViewViewModel() },
                accountId = 1, // TODO: Get from auth
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.OrderEdit.createRoute(id))
                }
            )
        }

        composable(Screen.Purchases.route) {
            com.sales.app.presentation.purchases.PurchasesScreen(
                viewModel = viewModel { appContainer.createPurchasesViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreatePurchase = {
                    navController.navigate(Screen.PurchaseCreate.route)
                },
                onNavigateToPurchaseDetails = { purchaseId ->
                    navController.navigate(Screen.PurchaseDetail.createRoute(purchaseId))
                }
            )
        }
        
        composable(Screen.PurchaseCreate.route) {
            com.sales.app.presentation.purchases.PurchaseFormScreen(
                viewModel = viewModel { appContainer.createPurchaseFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.PurchaseEdit.route,
            arguments = listOf(navArgument("purchaseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val purchaseId = backStackEntry.arguments?.getInt("purchaseId")
            com.sales.app.presentation.purchases.PurchaseFormScreen(
                viewModel = viewModel { appContainer.createPurchaseFormViewModel() },
                accountId = 1, // TODO: Get from auth
                purchaseId = purchaseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PurchaseDetail.route,
            arguments = listOf(navArgument("purchaseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val purchaseId = backStackEntry.arguments?.getInt("purchaseId") ?: return@composable
             com.sales.app.presentation.purchases.PurchaseViewScreen(
                viewModel = viewModel { appContainer.createPurchaseViewViewModel() },
                accountId = 1, // TODO: Get from auth
                purchaseId = purchaseId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.PurchaseEdit.createRoute(id))
                }
            )
        }

        // Inventory
        composable(Screen.Inventory.route) {
            InventoryScreen(
                viewModel = viewModel { appContainer.createInventoryViewModel() },
                stockAdjustmentViewModel = viewModel { appContainer.createStockAdjustmentViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SyncScreen(
                viewModel = viewModel { appContainer.createSyncViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AccountSettings.route) {
            com.sales.app.presentation.settings.AccountSettingsScreen(
                viewModel = viewModel { appContainer.createAccountSettingsViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Payments
        composable(Screen.Payments.route) {
            com.sales.app.presentation.payments.PaymentsScreen(
                viewModel = viewModel { appContainer.createPaymentsViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreatePayment = {
                    navController.navigate(Screen.PaymentCreate.route)
                }
            )
        }

        composable(Screen.PaymentCreate.route) {
            com.sales.app.presentation.payments.PaymentFormScreen(
                viewModel = viewModel { appContainer.createPaymentFormViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Price Lists
        composable(Screen.PriceLists.route) {
            com.sales.app.presentation.pricelists.PriceListsScreen(
                viewModel = viewModel { appContainer.createPriceListsViewModel() },
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.PriceListDetail.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.PriceListDetail.route,
            arguments = listOf(navArgument("priceListId") { type = NavType.LongType })
        ) { backStackEntry ->
            val priceListId = backStackEntry.arguments?.getLong("priceListId") ?: return@composable
            com.sales.app.presentation.pricelists.PriceListDetailScreen(
                viewModel = viewModel { appContainer.createPriceListDetailViewModel() },
                priceListId = priceListId,
                accountId = 1, // TODO: Get from auth
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
