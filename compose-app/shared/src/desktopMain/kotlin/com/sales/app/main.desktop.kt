package com.sales.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sales.app.di.AppContainer
import com.sales.app.di.createDataStore
import com.sales.app.di.createDatabase
import com.sales.app.di.createHttpClient

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() {
    val appContainer = remember {
        AppContainer(
            database = createDatabase(null),
            httpClient = createHttpClient(),
            dataStore = createDataStore(null)
        )
    }
    App(appContainer)
}

@Preview
@Composable
fun AppPreview() {
    val appContainer = AppContainer(
        database = createDatabase(null),
        httpClient = createHttpClient(),
        dataStore = createDataStore(null)
    )
    App(appContainer)
}
