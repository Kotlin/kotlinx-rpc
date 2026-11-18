/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
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
 * Runs [set], which applies the setting [name] of a configuration to [builder].
 *
 * When [set] throws [UnsupportedOperationException], [builder] doesn't support the setting:
 *
 * - [isSet] is `true`: the function throws a [GrpcUnsupportedSettingException] that names the setting.
 * - [isSet] is `false`: the function returns. The setting has the value of [builder].
 *
 * @param isSet `true` when the configuration block set the setting.
 * `false` when [set] applies the default of the setting.
 */
@InternalRpcApi
public inline fun applySetting(builder: Any, name: String, isSet: Boolean, set: () -> Unit) {
    try {
        set()
    } catch (e: UnsupportedOperationException) {
        if (isSet) {
            throw unsupportedSetting(builder, name, e)
        }
    }
}

@PublishedApi
internal fun unsupportedSetting(
    builder: Any,
    name: String,
    cause: UnsupportedOperationException,
): GrpcUnsupportedSettingException {
    return GrpcUnsupportedSettingException(
        "The builder `${builder::class.simpleName}` doesn't support `$name`. " +
            "Remove `$name` from the configuration block.",
        cause,
    )
}

/**
 * Throws [IllegalArgumentException] if a finite connection duration is below [minimum]
 * or at least `Int.MAX_VALUE` milliseconds. [Duration.INFINITE] always passes and means
 * "disabled" or "unlimited".
 *
 * All platforms enforce Kotlin/Native's C-core channel argument limit: a C `int`,
 * with `Int.MAX_VALUE` reserved for infinity. Larger values don't fit.
 */
@InternalRpcApi
public fun Duration.validateConnectionDuration(name: String, minimum: Duration) {
    if (this == Duration.INFINITE) {
        return
    }
    require(this >= minimum) { "$name must be at least $minimum" }
    require(inWholeMilliseconds < Int.MAX_VALUE) {
        "$name must be less than ${Int.MAX_VALUE} milliseconds"
    }
}
