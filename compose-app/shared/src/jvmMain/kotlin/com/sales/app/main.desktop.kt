package com.sales.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sales.app.di.SalesAppContainer
import com.sales.app.di.createDataStore
import com.sales.app.di.createDatabase
import com.sales.app.di.createHttpClient

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() {
    val appContainer = remember {
        SalesAppContainer(
            database = createDatabase(null),
            httpClient = createHttpClient(),
            dataStore = createDataStore(null)
        )
    }
    App(appContainer)
    
    // Initialize UnauthorizedHandler
    com.sales.app.di.unauthorizedHandler = com.sales.app.data.remote.UnauthorizedHandler(
        dataStore = appContainer.dataStore,
        userDao = appContainer.database.userDao(),
        onUnauthorized = {
            // Logic to handle navigation to login if needed, or just rely on state observers
            println("Unauthorized! Clearing session...")
        }
    )
}

@Preview
@Composable
fun AppPreview() {
    val appContainer = SalesAppContainer(
        database = createDatabase(null),
        httpClient = createHttpClient(),
        dataStore = createDataStore(null)
    )
    App(appContainer)
}
