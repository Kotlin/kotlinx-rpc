/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual fun StatusRuntimeException(status: Status): StatusRuntimeException {
    error("WasmJS target is not supported in gRPC")
}
