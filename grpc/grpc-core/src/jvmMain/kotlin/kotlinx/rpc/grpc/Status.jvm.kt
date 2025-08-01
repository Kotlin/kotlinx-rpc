/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.CyclomaticComplexMethod")

package kotlinx.rpc.grpc

internal fun StatusCode.toJvm(): io.grpc.Status.Code {
    return when (this) {
        StatusCode.OK -> io.grpc.Status.Code.OK
        StatusCode.CANCELLED -> io.grpc.Status.Code.CANCELLED
        StatusCode.UNKNOWN -> io.grpc.Status.Code.UNKNOWN
        StatusCode.INVALID_ARGUMENT -> io.grpc.Status.Code.INVALID_ARGUMENT
        StatusCode.DEADLINE_EXCEEDED -> io.grpc.Status.Code.DEADLINE_EXCEEDED
        StatusCode.NOT_FOUND -> io.grpc.Status.Code.NOT_FOUND
        StatusCode.ALREADY_EXISTS -> io.grpc.Status.Code.ALREADY_EXISTS
        StatusCode.PERMISSION_DENIED -> io.grpc.Status.Code.PERMISSION_DENIED
        StatusCode.RESOURCE_EXHAUSTED -> io.grpc.Status.Code.RESOURCE_EXHAUSTED
        StatusCode.FAILED_PRECONDITION -> io.grpc.Status.Code.FAILED_PRECONDITION
        StatusCode.ABORTED -> io.grpc.Status.Code.ABORTED
        StatusCode.OUT_OF_RANGE -> io.grpc.Status.Code.OUT_OF_RANGE
        StatusCode.UNIMPLEMENTED -> io.grpc.Status.Code.UNIMPLEMENTED
        StatusCode.INTERNAL -> io.grpc.Status.Code.INTERNAL
        StatusCode.UNAVAILABLE -> io.grpc.Status.Code.UNAVAILABLE
        StatusCode.DATA_LOSS -> io.grpc.Status.Code.DATA_LOSS
        StatusCode.UNAUTHENTICATED -> io.grpc.Status.Code.UNAUTHENTICATED
    }
}

public actual typealias Status = io.grpc.Status

public actual val Status.code: StatusCode
    get() = when (this.code) {
        io.grpc.Status.Code.OK -> StatusCode.OK
        io.grpc.Status.Code.CANCELLED -> StatusCode.CANCELLED
        io.grpc.Status.Code.UNKNOWN -> StatusCode.UNKNOWN
        io.grpc.Status.Code.INVALID_ARGUMENT -> StatusCode.INVALID_ARGUMENT
        io.grpc.Status.Code.DEADLINE_EXCEEDED -> StatusCode.DEADLINE_EXCEEDED
        io.grpc.Status.Code.NOT_FOUND -> StatusCode.NOT_FOUND
        io.grpc.Status.Code.ALREADY_EXISTS -> StatusCode.ALREADY_EXISTS
        io.grpc.Status.Code.PERMISSION_DENIED -> StatusCode.PERMISSION_DENIED
        io.grpc.Status.Code.RESOURCE_EXHAUSTED -> StatusCode.RESOURCE_EXHAUSTED
        io.grpc.Status.Code.FAILED_PRECONDITION -> StatusCode.FAILED_PRECONDITION
        io.grpc.Status.Code.ABORTED -> StatusCode.ABORTED
        io.grpc.Status.Code.OUT_OF_RANGE -> StatusCode.OUT_OF_RANGE
        io.grpc.Status.Code.UNIMPLEMENTED -> StatusCode.UNIMPLEMENTED
        io.grpc.Status.Code.INTERNAL -> StatusCode.INTERNAL
        io.grpc.Status.Code.UNAVAILABLE -> StatusCode.UNAVAILABLE
        io.grpc.Status.Code.DATA_LOSS -> StatusCode.DATA_LOSS
        io.grpc.Status.Code.UNAUTHENTICATED -> StatusCode.UNAUTHENTICATED
    }

public actual fun Status(
    code: StatusCode,
    description: String?,
    cause: Throwable?,
): Status {
    return Status.fromCode(code.toJvm()).withDescription(description).withCause(cause)
}
