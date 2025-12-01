package com.sales.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sales.app.di.AppContainer
import com.sales.app.presentation.home.HomeScreen
import com.sales.app.presentation.items.ItemsScreen
import com.sales.app.presentation.login.LoginScreen
import com.sales.app.presentation.parties.PartiesScreen
import com.sales.app.presentation.register.RegisterScreen
import com.sales.app.presentation.sync.SyncScreen
import com.sales.app.presentation.quotes.QuotesScreen
import com.sales.app.presentation.quotes.QuoteFormScreen
import com.sales.app.presentation.quotes.QuoteViewScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sales.app.presentation.items.ItemFormScreen
import com.sales.app.presentation.parties.PartyFormScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    appContainer: AppContainer
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
                onNavigateToSync = {
                    navController.navigate(Screen.Settings.route) // Using Settings route for Sync
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
        
        composable(Screen.Orders.route) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { androidx.compose.material3.Text("Orders (Not Implemented)") }
        }
        
        composable(Screen.Sales.route) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { androidx.compose.material3.Text("Sales (Not Implemented)") }
        }
        
        composable(Screen.Purchases.route) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { androidx.compose.material3.Text("Purchases (Not Implemented)") }
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
    }
}
