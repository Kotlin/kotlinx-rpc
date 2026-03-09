/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Exception used for propagating gRPC status information in non-OK results.
 *
 * This is the primary mechanism for reporting and handling errors in gRPC calls.
 * When a server encounters an error, it typically throws a [GrpcStatusException] with an appropriate
 * [GrpcStatusCode] to signal the failure to the client. Clients receive this exception when
 * remote calls fail with a non-OK status.
 *
 * The easiest way to construct a [GrpcStatusException] is to use the [GrpcStatusCode.asException] extension function:
 * ```
 * throw GrpcStatusCode.UNAUTHORIZED.asException("Authentication failed")
 * ```
 *
 * @see GrpcStatus
 * @see GrpcStatusCode
 */
public expect class GrpcStatusException : Exception {
    public constructor(status: GrpcStatus)
    public constructor(status: GrpcStatus, trailers: GrpcMetadata?)

    internal fun getStatus(): GrpcStatus
    internal fun getTrailers(): GrpcMetadata?
}

/**
 * The status associated with this exception.
 */
public val GrpcStatusException.status: GrpcStatus get() = getStatus()

/**
 * The trailing metadata associated with this exception, or null if not present.
 */
public val GrpcStatusException.trailers: GrpcMetadata? get() = getTrailers()

@InternalRpcApi
public expect class StatusRuntimeException : RuntimeException {
    internal constructor(status: GrpcStatus, trailers: GrpcMetadata?)

    internal fun getStatus(): GrpcStatus
    internal fun getTrailers(): GrpcMetadata?
}

@InternalRpcApi
public fun StatusRuntimeException(
    code: GrpcStatusCode,
    description: String? = null,
    trailers: GrpcMetadata? = null
): StatusRuntimeException {
    return StatusRuntimeException(GrpcStatus(code, description), trailers)
}

@InternalRpcApi
public val StatusRuntimeException.status: GrpcStatus get() = getStatus()

@InternalRpcApi
public val StatusRuntimeException.trailers: GrpcMetadata? get() = getTrailers()
