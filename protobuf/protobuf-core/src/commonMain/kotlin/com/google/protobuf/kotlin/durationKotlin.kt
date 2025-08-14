/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.google.protobuf.kotlin

import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

public fun Duration.toKotlinDuration(): kotlin.time.Duration = seconds.seconds + nanos.nanoseconds

public fun kotlin.time.Duration.toProtobufDuration(): Duration = Duration {
    seconds = this@toProtobufDuration.inWholeSeconds
    nanos = (this@toProtobufDuration.inWholeNanoseconds - seconds.seconds.inWholeNanoseconds).toInt()
}
