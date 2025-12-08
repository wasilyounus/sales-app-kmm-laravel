package com.sales.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sales.app.di.SalesAppContainer
import com.sales.app.presentation.navigation.AppNavigation
import com.sales.app.presentation.navigation.Login
import com.sales.app.presentation.theme.SalesAppTheme

@Composable
fun App(appContainer: SalesAppContainer) {
    SalesAppTheme {
        val navController = rememberNavController()
        
        AppNavigation(
            navController = navController,
            startDestination = Login,
            appContainer = appContainer
        )
    }
}

expect fun getPlatformName(): String
