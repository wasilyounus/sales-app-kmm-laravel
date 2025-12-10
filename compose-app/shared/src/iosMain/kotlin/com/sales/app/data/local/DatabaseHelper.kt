package com.sales.app.data.local

internal expect fun getDatabaseFactory(): () -> AppDatabase
