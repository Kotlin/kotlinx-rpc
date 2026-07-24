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
 * Validates a connection-management duration option.
 *
 * `null` and [Duration.INFINITE] are always accepted (both mean "disabled"/"unlimited");
 * finite values must be at least [minimum] and less than `Int.MAX_VALUE` milliseconds.
 *
 * The upper bound comes from Kotlin/Native, where these options become C-core channel args whose
 * values are a C `int`. [Duration.INFINITE] is passed as `Int.MAX_VALUE`, the largest value such
 * an arg can hold and the one gRPC treats as effectively unbounded; a finite value at or above it
 * would either be indistinguishable from that or not representable at all.
 *
 * The bound is enforced on every platform rather than only on native, so that the same
 * configuration is accepted or rejected identically everywhere — grpc-java itself takes a `long`
 * and would accept larger values.
 */
@InternalRpcApi
public fun Duration?.validateConnectionDuration(name: String, minimum: Duration) {
    if (this == null || this == Duration.INFINITE) return
    require(this >= minimum) { "$name must be at least $minimum" }
    require(inWholeMilliseconds < Int.MAX_VALUE) {
        "$name must be less than ${Int.MAX_VALUE} milliseconds"
    }
}
