/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * [Status] in Exception form, for propagating Status information via exceptions.
 */
public expect class StatusException : Exception {
    public constructor(status: Status)
    public constructor(status: Status, trailers: GrpcMetadata?)

    internal fun getStatus(): Status
    internal fun getTrailers(): GrpcMetadata?
}

public val StatusException.status: Status get() = getStatus()
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