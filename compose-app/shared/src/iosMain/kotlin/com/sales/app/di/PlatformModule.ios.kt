package com.sales.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sales.app.data.local.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import platform.Foundation.*
import io.ktor.client.plugins.defaultrequest.*

actual fun createDatabase(context: Any?): AppDatabase {
    val dbFilePath = NSHomeDirectory() + "/sales_app.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.Default) // Dispatchers.IO not available on Native? Use Default or custom
    .build()
}

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            val path = requireNotNull(documentDirectory).path + "/sales_app.preferences_pb"
            path.toPath()
        }
    )
}



actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(DefaultRequest) {
            url("http://192.168.1.174:8000/api/")
        }
    }
}
