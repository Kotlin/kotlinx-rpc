/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

internal fun GrpcStatusCode.toJvm(): io.grpc.Status.Code {
    return when (this) {
        GrpcStatusCode.OK -> io.grpc.Status.Code.OK
        GrpcStatusCode.CANCELLED -> io.grpc.Status.Code.CANCELLED
        GrpcStatusCode.UNKNOWN -> io.grpc.Status.Code.UNKNOWN
        GrpcStatusCode.INVALID_ARGUMENT -> io.grpc.Status.Code.INVALID_ARGUMENT
        GrpcStatusCode.DEADLINE_EXCEEDED -> io.grpc.Status.Code.DEADLINE_EXCEEDED
        GrpcStatusCode.NOT_FOUND -> io.grpc.Status.Code.NOT_FOUND
        GrpcStatusCode.ALREADY_EXISTS -> io.grpc.Status.Code.ALREADY_EXISTS
        GrpcStatusCode.PERMISSION_DENIED -> io.grpc.Status.Code.PERMISSION_DENIED
        GrpcStatusCode.RESOURCE_EXHAUSTED -> io.grpc.Status.Code.RESOURCE_EXHAUSTED
        GrpcStatusCode.FAILED_PRECONDITION -> io.grpc.Status.Code.FAILED_PRECONDITION
        GrpcStatusCode.ABORTED -> io.grpc.Status.Code.ABORTED
        GrpcStatusCode.OUT_OF_RANGE -> io.grpc.Status.Code.OUT_OF_RANGE
        GrpcStatusCode.UNIMPLEMENTED -> io.grpc.Status.Code.UNIMPLEMENTED
        GrpcStatusCode.INTERNAL -> io.grpc.Status.Code.INTERNAL
        GrpcStatusCode.UNAVAILABLE -> io.grpc.Status.Code.UNAVAILABLE
        GrpcStatusCode.DATA_LOSS -> io.grpc.Status.Code.DATA_LOSS
        GrpcStatusCode.UNAUTHENTICATED -> io.grpc.Status.Code.UNAUTHENTICATED
    }
}

public actual typealias GrpcStatus = io.grpc.Status

public actual val GrpcStatus.statusCode: GrpcStatusCode
    get() = when (this.code) {
        io.grpc.Status.Code.OK -> GrpcStatusCode.OK
        io.grpc.Status.Code.CANCELLED -> GrpcStatusCode.CANCELLED
        io.grpc.Status.Code.UNKNOWN -> GrpcStatusCode.UNKNOWN
        io.grpc.Status.Code.INVALID_ARGUMENT -> GrpcStatusCode.INVALID_ARGUMENT
        io.grpc.Status.Code.DEADLINE_EXCEEDED -> GrpcStatusCode.DEADLINE_EXCEEDED
        io.grpc.Status.Code.NOT_FOUND -> GrpcStatusCode.NOT_FOUND
        io.grpc.Status.Code.ALREADY_EXISTS -> GrpcStatusCode.ALREADY_EXISTS
        io.grpc.Status.Code.PERMISSION_DENIED -> GrpcStatusCode.PERMISSION_DENIED
        io.grpc.Status.Code.RESOURCE_EXHAUSTED -> GrpcStatusCode.RESOURCE_EXHAUSTED
        io.grpc.Status.Code.FAILED_PRECONDITION -> GrpcStatusCode.FAILED_PRECONDITION
        io.grpc.Status.Code.ABORTED -> GrpcStatusCode.ABORTED
        io.grpc.Status.Code.OUT_OF_RANGE -> GrpcStatusCode.OUT_OF_RANGE
        io.grpc.Status.Code.UNIMPLEMENTED -> GrpcStatusCode.UNIMPLEMENTED
        io.grpc.Status.Code.INTERNAL -> GrpcStatusCode.INTERNAL
        io.grpc.Status.Code.UNAVAILABLE -> GrpcStatusCode.UNAVAILABLE
        io.grpc.Status.Code.DATA_LOSS -> GrpcStatusCode.DATA_LOSS
        io.grpc.Status.Code.UNAUTHENTICATED -> GrpcStatusCode.UNAUTHENTICATED
    }

public actual fun GrpcStatus(
    code: GrpcStatusCode,
    description: String?,
    cause: Throwable?,
): GrpcStatus {
    return GrpcStatus.fromCode(code.toJvm()).withDescription(description).withCause(cause)
}
