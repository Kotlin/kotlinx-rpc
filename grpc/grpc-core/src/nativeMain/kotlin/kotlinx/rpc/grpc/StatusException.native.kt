/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual class StatusException : Exception {
    private val status: Status
    private val trailers: GrpcMetadata?

    public actual constructor(status: Status) : this(status, null)

    public actual constructor(status: Status, trailers: GrpcMetadata?) : super(
        "${status.code}: ${status.getDescription()}",
        status.getCause()
    ) {
        this.status = status
        this.trailers = trailers
    }

    public actual fun getStatus(): Status = status

    public actual fun getTrailers(): GrpcMetadata? = trailers
}

public actual class StatusRuntimeException : RuntimeException {
    private val status: Status
    private val trailers: GrpcMetadata?

    public actual constructor(status: Status) : this(status, null)

    public actual constructor(status: Status, trailers: GrpcMetadata?) : super(
        "${status.code}: ${status.getDescription()}",
        status.getCause()
    ) {
        this.status = status
        this.trailers = trailers
    }

    public actual fun getStatus(): Status = status

    public actual fun getTrailers(): GrpcMetadata? = trailers
}
