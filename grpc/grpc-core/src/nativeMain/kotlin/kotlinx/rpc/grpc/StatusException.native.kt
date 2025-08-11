/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual class StatusException : Exception {
    private val status: Status
    private val trailers: GrpcTrailers?

    public actual constructor(status: Status) : super(status.getDescription()) {
        this.status = status
        this.trailers = null
    }

    public actual constructor(status: Status, trailers: GrpcTrailers?) : super(status.getDescription()) {
        this.status = status
        this.trailers = trailers
    }

    public actual fun getStatus(): Status = status

    public actual fun getTrailers(): GrpcTrailers? = trailers
}

public actual class StatusRuntimeException : RuntimeException {
    private val status: Status
    private val trailers: GrpcTrailers?

    public actual constructor(status: Status) : super(status.getDescription()) {
        this.status = status
        this.trailers = null
    }

    public actual constructor(status: Status, trailers: GrpcTrailers?) : super(status.getDescription()) {
        this.status = status
        this.trailers = trailers
    }

    public actual fun getStatus(): Status = status

    public actual fun getTrailers(): GrpcTrailers? = trailers
}
