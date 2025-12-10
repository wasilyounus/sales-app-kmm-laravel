package com.sales.app.data.local

internal actual fun getDatabaseFactory(): () -> AppDatabase = { AppDatabase::class.instantiateImpl() }
