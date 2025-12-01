package com.sales.app

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.sales.app.di.AppContainer
import com.sales.app.di.createDataStore
import com.sales.app.di.createDatabase
import com.sales.app.di.createHttpClient

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController {
    val appContainer = remember {
        AppContainer(
            database = createDatabase(null),
            httpClient = createHttpClient(),
            dataStore = createDataStore(null)
        )
    }
    App(appContainer)
}
