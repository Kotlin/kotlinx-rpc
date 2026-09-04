/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class GrpcStatusException : Exception {
    public actual constructor(status: GrpcStatus) : super(
        message = TODO("Implement iOS gRPC status exceptions")
    )

    public actual constructor(
        status: GrpcStatus,
        trailers: GrpcMetadata?,
    ) : super(message = TODO("Implement iOS gRPC status exceptions"))

    internal actual fun getStatus(): GrpcStatus = TODO("Implement iOS gRPC status exceptions")

    internal actual fun getTrailers(): GrpcMetadata? = TODO("Implement iOS gRPC status exceptions")
}

@InternalRpcApi
public actual class StatusRuntimeException : RuntimeException {
    internal actual constructor(
        status: GrpcStatus,
        trailers: GrpcMetadata?,
    ) : super(message = TODO("Implement iOS gRPC status exceptions"))

    internal actual fun getStatus(): GrpcStatus = TODO("Implement iOS gRPC status exceptions")

    internal actual fun getTrailers(): GrpcMetadata? = TODO("Implement iOS gRPC status exceptions")
}
