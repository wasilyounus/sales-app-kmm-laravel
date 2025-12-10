package com.sales.app.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual object TimeProvider {
    actual fun now(): Instant = Clock.System.now()
}
