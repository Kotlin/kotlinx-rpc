/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.client.GrpcCallCredentials

internal actual fun GrpcClientTransport(
    channel: ManagedChannel,
    callCredentials: GrpcCallCredentials,
): GrpcClientTransport {
    TODO("Not yet implemented")
}
