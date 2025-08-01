/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * [Status] in Exception form, for propagating Status information via exceptions.
 */
public expect class StatusException : Exception {
    public constructor(status: Status)
    public constructor(status: Status, trailers: GrpcTrailers?)

    public fun getStatus(): Status
    public fun getTrailers(): GrpcTrailers?
}

public expect class StatusRuntimeException : RuntimeException {
    public constructor(status: Status)
    public constructor(status: Status, trailers: GrpcTrailers?)

    public fun getStatus(): Status
    public fun getTrailers(): GrpcTrailers?
}
