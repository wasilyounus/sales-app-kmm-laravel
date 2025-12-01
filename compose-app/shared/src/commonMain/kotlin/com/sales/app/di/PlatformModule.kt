package com.sales.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sales.app.data.local.AppDatabase
import io.ktor.client.HttpClient

expect fun createDatabase(context: Any?): AppDatabase

expect fun createDataStore(context: Any?): DataStore<Preferences>

expect fun createHttpClient(): HttpClient
