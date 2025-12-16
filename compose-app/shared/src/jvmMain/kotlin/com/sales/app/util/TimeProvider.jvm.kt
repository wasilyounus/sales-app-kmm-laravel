package com.sales.app.util

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object TimeProvider {
    actual fun now(): Instant = Clock.System.now()
}
