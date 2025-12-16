@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.util

import kotlin.time.Clock
import kotlin.time.Instant

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object TimeProvider {
    actual fun now(): Instant = Clock.System.now()
}
