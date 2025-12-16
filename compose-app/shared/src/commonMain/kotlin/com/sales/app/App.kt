package com.sales.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.rememberNavController
import com.sales.app.di.SalesAppContainer
import com.sales.app.presentation.navigation.AppNavigation
import com.sales.app.presentation.navigation.Login
import com.sales.app.presentation.theme.SalesAppTheme
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun App(appContainer: SalesAppContainer) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val companyState = appContainer.authRepository.currentUser()
        .map { it?.currentCompanyId }
        .distinctUntilChanged()
        .flatMapLatest { id ->
            if (id != null) {
                appContainer.companyRepository.getCompanyById(id)
            } else {
                appContainer.companyRepository.getCompany()
            }
        }
        .collectAsState(initial = null)
    val isDarkTheme = companyState.value?.darkMode ?: isSystemInDarkTheme()

    SalesAppTheme(darkTheme = isDarkTheme) {
        val navController = rememberNavController()
        
        AppNavigation(
            navController = navController,
            startDestination = Login,
            appContainer = appContainer
        )
    }
}

expect fun getPlatformName(): String
