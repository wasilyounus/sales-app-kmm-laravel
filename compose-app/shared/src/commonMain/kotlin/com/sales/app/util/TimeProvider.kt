@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.util

import kotlinx.datetime.Instant

expect object TimeProvider {
    fun now(): Instant
}
