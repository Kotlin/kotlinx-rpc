/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.time.Duration

@InternalRpcApi
public fun internalError(message: String): Nothing {
    error(
        "Unexpected internal error: $message. " +
            "Please, report the issue here: " +
            "https://github.com/Kotlin/kotlinx-rpc/issues/new?template=bug_report.md"
    )
}

/**
 * Throws [IllegalArgumentException] when a connection duration option is out of range.
 *
 * `null` and [Duration.INFINITE] always pass. They mean "disabled" or "unlimited".
 * A finite value must be at least [minimum] and less than `Int.MAX_VALUE` milliseconds.
 *
 * The upper bound comes from Kotlin/Native. A C-core channel arg is a C `int`, and
 * [Duration.INFINITE] maps to `Int.MAX_VALUE`. A finite value at or above `Int.MAX_VALUE`
 * either equals infinite or doesn't fit.
 *
 * Every platform enforces the bound, so a configuration behaves the same everywhere.
 * On the JVM, grpc-java alone accepts larger values, because it takes a `long`.
 */
@InternalRpcApi
public fun Duration?.validateConnectionDuration(name: String, minimum: Duration) {
    if (this == null || this == Duration.INFINITE) return
    require(this >= minimum) { "$name must be at least $minimum" }
    require(inWholeMilliseconds < Int.MAX_VALUE) {
        "$name must be less than ${Int.MAX_VALUE} milliseconds"
    }
}
