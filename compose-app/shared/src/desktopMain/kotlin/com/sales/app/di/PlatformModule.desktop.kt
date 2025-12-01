package com.sales.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sales.app.data.local.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import java.io.File

actual fun createDatabase(context: Any?): AppDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "sales_app.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
    .setDriver(BundledSQLiteDriver())
    .fallbackToDestructiveMigration(true)
    .build()
}

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            File(System.getProperty("java.io.tmpdir"), "sales_app.preferences_pb").absolutePath.toPath()
        }
    )
}

actual fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        // Configure base URL for desktop (localhost)
        install(DefaultRequest) {
            url("http://192.168.1.174:8000/api/")
            
            // Default headers
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
        
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
}
