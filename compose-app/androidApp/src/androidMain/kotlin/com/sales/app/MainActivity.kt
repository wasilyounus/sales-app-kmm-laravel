package com.sales.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.sales.app.util.AndroidPlatformContext.set(this)
        setContent {
            MainView()
        }
    }
}
