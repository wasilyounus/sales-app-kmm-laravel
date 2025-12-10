@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.util

import kotlinx.datetime.Instant

actual object TimeProvider {
    actual fun now(): Instant = kotlinx.datetime.Clock.System.now()
}
