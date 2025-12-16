@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.sales.app.util

import kotlin.time.Instant

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object TimeProvider {
    fun now(): Instant
}
