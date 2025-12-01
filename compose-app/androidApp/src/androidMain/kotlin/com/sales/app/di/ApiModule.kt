package com.sales.app.di

import com.sales.app.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    
    @Provides
    @Singleton
    fun provideApiService(
        client: HttpClient,
        dataStore: DataStore<Preferences>
    ): ApiService {
        return ApiService(client) {
            dataStore.data.map { preferences ->
                preferences[stringPreferencesKey("auth_token")]
            }.firstOrNull()
        }
    }
}
