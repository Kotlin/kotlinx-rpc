/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class GrpcStatusException : Exception {
    private val status: GrpcStatus
    private val trailers: GrpcMetadata?

    public actual constructor(status: GrpcStatus) : this(status, null)

    public actual constructor(status: GrpcStatus, trailers: GrpcMetadata?) : super(
        "${status.statusCode}: ${status.getDescription()}",
        status.getCause()
    ) {
        this.status = status
        this.trailers = trailers
    }

    internal actual fun getStatus(): GrpcStatus = status

    internal actual fun getTrailers(): GrpcMetadata? = trailers
}

@InternalRpcApi
public actual class StatusRuntimeException : RuntimeException {
    private val status: GrpcStatus
    private val trailers: GrpcMetadata?

    internal actual constructor(status: GrpcStatus, trailers: GrpcMetadata?) : super(
        "${status.statusCode}: ${status.getDescription()}",
        status.getCause()
    ) {
        this.status = status
        this.trailers = trailers
    }

    internal actual fun getStatus(): GrpcStatus = status

    internal actual fun getTrailers(): GrpcMetadata? = trailers
}
