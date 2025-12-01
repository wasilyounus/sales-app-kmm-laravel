package com.sales.app.di

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    //private const val BASE_URL = "http://10.0.2.2:8000/api" // Android emulator localhost
    private const val BASE_URL = "http://192.168.1.174:8000/api" // Android emulator localhost

    // For physical device, use: "http://YOUR_IP:8000/api"
    
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = true
    }
    
    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        dataStore: DataStore<Preferences>
    ): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }
            
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.ALL
            }
            
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = runBlocking {
                            dataStore.data.map { preferences ->
                                preferences[stringPreferencesKey("auth_token")]
                            }.first()
                        }
                        token?.let {
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }
                }
            }
            
            install(DefaultRequest) {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }
        }
    }
}
