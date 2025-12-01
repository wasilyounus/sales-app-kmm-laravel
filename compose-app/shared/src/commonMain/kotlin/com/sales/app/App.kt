package com.sales.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sales.app.di.AppContainer
import com.sales.app.presentation.navigation.AppNavigation
import com.sales.app.presentation.navigation.Screen
import com.sales.app.presentation.theme.SalesAppTheme

@Composable
fun App(appContainer: AppContainer) {
    SalesAppTheme {
        val navController = rememberNavController()
        
        AppNavigation(
            navController = navController,
            startDestination = Screen.Login.route,
            appContainer = appContainer
        )
    }
}

expect fun getPlatformName(): String
