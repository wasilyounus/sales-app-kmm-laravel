package com.sales.app.config

/**
 * Build configuration injected at compile time from root .env file.
 * To update, modify the .env file in the project root and rebuild.
 * 
 * Generated - do not edit manually!
 */
object BuildConfig {
    // Environment
    const val APP_ENV: String = "development"
    const val IS_PRODUCTION: Boolean = false
    const val IS_DEBUG: Boolean = true
    
    // API Configuration
    const val API_PROTOCOL: String = "http"
    const val API_HOST: String = "192.168.1.131"
    const val API_PORT: String = "8000"
    const val API_BASE_URL: String = "$API_PROTOCOL://$API_HOST:$API_PORT/api/"
}