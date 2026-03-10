/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlin.coroutines.CoroutineContext

/**
 * Platform abstraction for the underlying gRPC call context associated with the current server call.
 *
 * On JVM this is backed by `io.grpc.Context` and can be used to preserve gRPC call-local state across coroutine
 * boundaries.
 *
 * On Native targets this type currently exists only as a placeholder for API parity. Native gRPC context
 * propagation is not implemented yet, so the current instance does not carry a real call-scoped state.
 */
public expect class GrpcContext

internal expect val CurrentGrpcContext: GrpcContext

internal expect class GrpcContextElement : CoroutineContext.Element {
    val grpcContext: GrpcContext

    companion object Key : CoroutineContext.Key<GrpcContextElement> {
        fun current(): GrpcContextElement
    }

    override val key: CoroutineContext.Key<GrpcContextElement>
}
