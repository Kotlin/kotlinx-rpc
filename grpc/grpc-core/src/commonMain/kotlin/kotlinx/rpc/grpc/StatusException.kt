/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Exception used for propagating gRPC status information in non-OK results.
 *
 * This is the primary mechanism for reporting and handling errors in gRPC calls.
 * When a server encounters an error, it typically throws a [StatusException] with an appropriate
 * [StatusCode] to signal the failure to the client. Clients receive this exception when
 * remote calls fail with a non-OK status.
 *
 * The easiest way to construct a [StatusException] is to use the [StatusCode.asException] extension function:
 * ```
 * throw StatusCode.UNAUTHORIZED.asException("Authentication failed")
 * ```
 *
 * @see Status
 * @see StatusCode
 */
public expect class StatusException : Exception {
    public constructor(status: Status)
    public constructor(status: Status, trailers: GrpcMetadata?)

    internal fun getStatus(): Status
    internal fun getTrailers(): GrpcMetadata?
}

/**
 * The status associated with this exception.
 */
public val StatusException.status: Status get() = getStatus()

/**
 * The trailing metadata associated with this exception, or null if not present.
 */
public val StatusException.trailers: GrpcMetadata? get() = getTrailers()

@InternalRpcApi
public expect class StatusRuntimeException : RuntimeException {
    internal constructor(status: Status, trailers: GrpcMetadata?)

    internal fun getStatus(): Status
    internal fun getTrailers(): GrpcMetadata?
}

@InternalRpcApi
public fun StatusRuntimeException(code: StatusCode, description: String? = null, trailers: GrpcMetadata? = null): StatusRuntimeException {
    return StatusRuntimeException(Status(code, description), trailers)
}

@InternalRpcApi
public val StatusRuntimeException.status: Status get() = getStatus()
@InternalRpcApi
public val StatusRuntimeException.trailers: GrpcMetadata? get() = getTrailers()