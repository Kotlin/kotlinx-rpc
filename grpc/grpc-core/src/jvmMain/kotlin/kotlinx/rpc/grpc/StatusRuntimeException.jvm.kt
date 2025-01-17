/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual fun StatusRuntimeException(status: Status): StatusRuntimeException {
    return io.grpc.StatusRuntimeException(status.toJvm()).toKotlin()
}

internal class JvmStatusRuntimeException(override val status: Status) : StatusRuntimeException

public fun io.grpc.StatusRuntimeException.toKotlin(): StatusRuntimeException {
    return JvmStatusRuntimeException(status.toKotlin())
}

public fun StatusRuntimeException.toJvm(): io.grpc.StatusRuntimeException {
    return io.grpc.StatusRuntimeException(status.toJvm())
}
