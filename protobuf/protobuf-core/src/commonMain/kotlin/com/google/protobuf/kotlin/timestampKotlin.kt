/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalTime::class)

package com.google.protobuf.kotlin

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public fun Timestamp.toInstant(): Instant = Instant.fromEpochSeconds(seconds, nanos)

public fun Instant.toTimestamp(): Timestamp = Timestamp {
    seconds = epochSeconds
    nanos = nanosecondsOfSecond
}
