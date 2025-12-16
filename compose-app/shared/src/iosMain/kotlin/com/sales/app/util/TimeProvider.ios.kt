@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.util

import kotlin.time.Instant
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object TimeProvider {
    actual fun now(): Instant {
        val seconds = NSDate().timeIntervalSince1970
        return Instant.fromEpochMilliseconds((seconds * 1000).toLong())
    }
}
