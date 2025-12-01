package com.sales.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SalesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
