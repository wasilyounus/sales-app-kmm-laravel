@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.util

import kotlinx.datetime.Instant
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object TimeProvider {
    actual fun now(): Instant {
        val seconds = NSDate().timeIntervalSince1970
        return Instant.fromEpochMilliseconds((seconds * 1000).toLong())
    }
}
