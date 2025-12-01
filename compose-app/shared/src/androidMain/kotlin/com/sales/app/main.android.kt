package com.sales.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.sales.app.di.AppContainer
import com.sales.app.di.createDataStore
import com.sales.app.di.createDatabase
import com.sales.app.di.createHttpClient
import com.sales.app.di.unauthorizedHandler
import com.sales.app.data.remote.UnauthorizedHandler
import com.sales.app.presentation.navigation.AppNavigation
import com.sales.app.presentation.navigation.Screen
import com.sales.app.presentation.theme.SalesAppTheme

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() {
    val context = LocalContext.current
    val navController = rememberNavController()
    var shouldNavigateToLogin by remember { mutableStateOf(false) }
    
    val appContainer = remember {
        val dataStore = createDataStore(context)
        val database = createDatabase(context)
        
        // Initialize unauthorized handler
        unauthorizedHandler = UnauthorizedHandler(
            dataStore = dataStore,
            userDao = database.userDao(),
            onUnauthorized = {
                shouldNavigateToLogin = true
            }
        )
        
        AppContainer(
            database = database,
            httpClient = createHttpClient(),
            dataStore = dataStore
        )
    }
    
    // Handle navigation to login when unauthorized
    LaunchedEffect(shouldNavigateToLogin) {
        if (shouldNavigateToLogin) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationRoute ?: Screen.Login.route) {
                    inclusive = true
                }
            }
            shouldNavigateToLogin = false
        }
    }
    
    SalesAppTheme {
        AppNavigation(
            navController = navController,
            startDestination = Screen.Login.route,
            appContainer = appContainer
        )
    }
}
