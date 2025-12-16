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
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import com.sales.app.data.remote.UnauthorizedHandler

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



// Global unauthorized handler - will be set by App
var unauthorizedHandler: UnauthorizedHandler? = null

actual fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        
        // Handle 401 Unauthorized responses
        HttpResponseValidator {
            validateResponse { response: HttpResponse ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    unauthorizedHandler?.handle()
                    throw io.ktor.client.plugins.ClientRequestException(response, response.bodyAsText())
                }
            }
        }
        
        // Configure base URL from BuildConfig (generated from root .env)
        install(DefaultRequest) {
            url(com.sales.app.config.BuildConfig.API_BASE_URL)
            
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
