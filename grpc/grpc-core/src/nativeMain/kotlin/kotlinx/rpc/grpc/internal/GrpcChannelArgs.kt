/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.ptr
import kotlinx.rpc.grpc.internal.cinterop.grpc_arg
import kotlinx.rpc.grpc.internal.cinterop.grpc_arg_type
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_args
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.time.Duration

// C-core channel-arg keys shared by the native client and server.
@InternalRpcApi
public const val GRPC_ARG_MAX_RECEIVE_MESSAGE_LENGTH: String = "grpc.max_receive_message_length"

@InternalRpcApi
public const val GRPC_ARG_MAX_METADATA_SIZE: String = "grpc.max_metadata_size"

@InternalRpcApi
public const val GRPC_ARG_ABSOLUTE_MAX_METADATA_SIZE: String = "grpc.absolute_max_metadata_size"

@InternalRpcApi
public const val GRPC_ARG_KEEPALIVE_TIME_MS: String = "grpc.keepalive_time_ms"

@InternalRpcApi
public const val GRPC_ARG_KEEPALIVE_TIMEOUT_MS: String = "grpc.keepalive_timeout_ms"

@InternalRpcApi
public const val GRPC_ARG_KEEPALIVE_PERMIT_WITHOUT_CALLS: String = "grpc.keepalive_permit_without_calls"

@InternalRpcApi
public const val GRPC_ARG_CLIENT_IDLE_TIMEOUT_MS: String = "grpc.client_idle_timeout_ms"

@InternalRpcApi
public const val GRPC_ARG_MAX_CONNECTION_IDLE_MS: String = "grpc.max_connection_idle_ms"

@InternalRpcApi
public const val GRPC_ARG_MAX_CONNECTION_AGE_MS: String = "grpc.max_connection_age_ms"

@InternalRpcApi
public const val GRPC_ARG_MAX_CONNECTION_AGE_GRACE_MS: String = "grpc.max_connection_age_grace_ms"

/**
 * A single C-core channel argument.
 */
@InternalRpcApi
public sealed class GrpcArg(public val key: String) {
    public class Str(key: String, public val value: String) : GrpcArg(key)
    public class Integer(key: String, public val value: Int) : GrpcArg(key)

    internal val rawType: grpc_arg_type
        get() = when (this) {
            is Str -> grpc_arg_type.GRPC_ARG_STRING
            is Integer -> grpc_arg_type.GRPC_ARG_INTEGER
        }
}

/**
 * Converts a duration to a millisecond channel-arg value, mapping [Duration.INFINITE] to
 * `INT_MAX` — the largest value a C-core channel arg can hold, which gRPC treats as
 * effectively unbounded.
 *
 * Channel-arg values are a C `int`, so this narrows [Duration.inWholeMilliseconds] from `Long`.
 * A finite duration of `Int.MAX_VALUE` milliseconds or more (~24.8 days) has no representation
 * and would wrap silently — possibly to a negative value, or exactly onto the `INT_MAX` that
 * a caller means as "unbounded" — so it is rejected here rather than truncated.
 *
 * Every public option funnels through [validateConnectionDuration], which rejects out-of-range
 * values earlier with a message naming the option. This check is the backstop for the paths that
 * do not: property defaults, which bypass setters, and any future caller of this function.
 */
@InternalRpcApi
public fun Duration.toChannelArgMilliseconds(): Int {
    if (this == Duration.INFINITE) return Int.MAX_VALUE

    val millis = inWholeMilliseconds
    require(millis in 0 until Int.MAX_VALUE.toLong()) {
        "duration must be between 0 and ${Int.MAX_VALUE} milliseconds (exclusive), was $this"
    }
    return millis.convert()
}

/**
 * Marshals the args into a C-core [grpc_channel_args] allocated in [memScope].
 *
 * [GrpcArg.Integer] values are converted to C `int`, which is 32-bit on every platform
 * kotlinx-rpc targets, so the conversion is lossless there.
 */
@InternalRpcApi
public fun List<GrpcArg>.toRaw(memScope: MemScope): grpc_channel_args {
    with(memScope) {
        val arr = allocArray<grpc_arg>(size) {
            val arg = get(it)
            type = arg.rawType
            key = arg.key.cstr.ptr
            when (arg) {
                is GrpcArg.Str -> value.string = arg.value.cstr.ptr
                is GrpcArg.Integer -> value.integer = arg.value.convert()
            }
        }

        return alloc<grpc_channel_args> {
            num_args = size.convert()
            args = arr
        }
    }
}
