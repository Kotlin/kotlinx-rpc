/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual class StatusException : Exception {
    public actual fun getStatus(): Status {
        TODO("Not yet implemented")
    }

    public actual fun getTrailers(): GrpcTrailers? {
        TODO("Not yet implemented")
    }

    public actual constructor(status: Status) {
        TODO("Not yet implemented")
    }

    public actual constructor(status: Status, trailers: GrpcTrailers?) {
        TODO("Not yet implemented")
    }
}

public actual class StatusRuntimeException : RuntimeException {
    public actual fun getStatus(): Status {
        TODO("Not yet implemented")
    }

    public actual fun getTrailers(): GrpcTrailers? {
        TODO("Not yet implemented")
    }

    public actual constructor(status: Status) {
        TODO("Not yet implemented")
    }

    public actual constructor(status: Status, trailers: GrpcTrailers?) {
        TODO("Not yet implemented")
    }
}
