/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.CyclomaticComplexMethod")

package kotlinx.rpc.grpc

internal fun Status.toJvm(): io.grpc.Status {
    val code = when (code) {
        Status.Code.OK -> io.grpc.Status.Code.OK
        Status.Code.CANCELLED -> io.grpc.Status.Code.CANCELLED
        Status.Code.UNKNOWN -> io.grpc.Status.Code.UNKNOWN
        Status.Code.INVALID_ARGUMENT -> io.grpc.Status.Code.INVALID_ARGUMENT
        Status.Code.DEADLINE_EXCEEDED -> io.grpc.Status.Code.DEADLINE_EXCEEDED
        Status.Code.NOT_FOUND -> io.grpc.Status.Code.NOT_FOUND
        Status.Code.ALREADY_EXISTS -> io.grpc.Status.Code.ALREADY_EXISTS
        Status.Code.PERMISSION_DENIED -> io.grpc.Status.Code.PERMISSION_DENIED
        Status.Code.RESOURCE_EXHAUSTED -> io.grpc.Status.Code.RESOURCE_EXHAUSTED
        Status.Code.FAILED_PRECONDITION -> io.grpc.Status.Code.FAILED_PRECONDITION
        Status.Code.ABORTED -> io.grpc.Status.Code.ABORTED
        Status.Code.OUT_OF_RANGE -> io.grpc.Status.Code.OUT_OF_RANGE
        Status.Code.UNIMPLEMENTED -> io.grpc.Status.Code.UNIMPLEMENTED
        Status.Code.INTERNAL -> io.grpc.Status.Code.INTERNAL
        Status.Code.UNAVAILABLE -> io.grpc.Status.Code.UNAVAILABLE
        Status.Code.DATA_LOSS -> io.grpc.Status.Code.DATA_LOSS
        Status.Code.UNAUTHENTICATED -> io.grpc.Status.Code.UNAUTHENTICATED
    }

    return io.grpc.Status.fromCode(code)
        .withDescription(description)
        .withCause(cause)
}

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
internal fun io.grpc.Status.toKotlin(): Status {
    val code = when (code) {
        io.grpc.Status.Code.OK -> Status.Code.OK
        io.grpc.Status.Code.CANCELLED -> Status.Code.CANCELLED
        io.grpc.Status.Code.UNKNOWN -> Status.Code.UNKNOWN
        io.grpc.Status.Code.INVALID_ARGUMENT -> Status.Code.INVALID_ARGUMENT
        io.grpc.Status.Code.DEADLINE_EXCEEDED -> Status.Code.DEADLINE_EXCEEDED
        io.grpc.Status.Code.NOT_FOUND -> Status.Code.NOT_FOUND
        io.grpc.Status.Code.ALREADY_EXISTS -> Status.Code.ALREADY_EXISTS
        io.grpc.Status.Code.PERMISSION_DENIED -> Status.Code.PERMISSION_DENIED
        io.grpc.Status.Code.RESOURCE_EXHAUSTED -> Status.Code.RESOURCE_EXHAUSTED
        io.grpc.Status.Code.FAILED_PRECONDITION -> Status.Code.FAILED_PRECONDITION
        io.grpc.Status.Code.ABORTED -> Status.Code.ABORTED
        io.grpc.Status.Code.OUT_OF_RANGE -> Status.Code.OUT_OF_RANGE
        io.grpc.Status.Code.UNIMPLEMENTED -> Status.Code.UNIMPLEMENTED
        io.grpc.Status.Code.INTERNAL -> Status.Code.INTERNAL
        io.grpc.Status.Code.UNAVAILABLE -> Status.Code.UNAVAILABLE
        io.grpc.Status.Code.DATA_LOSS -> Status.Code.DATA_LOSS
        io.grpc.Status.Code.UNAUTHENTICATED -> Status.Code.UNAUTHENTICATED
    }

    return JvmStatus(code, description, cause)
}

internal class JvmStatus(
    override val code: Status.Code,
    override val description: String? = null,
    override val cause: Throwable? = null,
): Status
