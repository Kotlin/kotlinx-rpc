/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual class GrpcStatus internal constructor(
    description: String?,
    statusCode: GrpcStatusCode,
    cause: Throwable?,
) {
    public actual fun getDescription(): String? = TODO("Implement iOS gRPC status")

    public actual fun getCause(): Throwable? = TODO("Implement iOS gRPC status")

    override fun toString(): String = TODO("Implement iOS gRPC status")
}

public actual fun GrpcStatus(
    code: GrpcStatusCode,
    description: String?,
    cause: Throwable?,
): GrpcStatus = TODO("Implement iOS gRPC status")

public actual val GrpcStatus.statusCode: GrpcStatusCode
    get() = TODO("Implement iOS gRPC status")
