/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import io.grpc.Context
import kotlinx.coroutines.ThreadContextElement
import kotlinx.rpc.grpc.server.internal.CurrentGrpcContext
import kotlinx.rpc.grpc.server.internal.GrpcContext
import kotlinx.rpc.grpc.server.internal.GrpcContextElement
import kotlin.coroutines.CoroutineContext

public actual typealias GrpcContext = Context

internal actual val CurrentGrpcContext: GrpcContext
    get() = GrpcContext.current()

internal actual class GrpcContextElement(actual val grpcContext: GrpcContext) : ThreadContextElement<GrpcContext> {
    actual companion object Key : CoroutineContext.Key<GrpcContextElement> {
        actual fun current(): GrpcContextElement = GrpcContextElement(CurrentGrpcContext)
    }

    actual override val key: CoroutineContext.Key<GrpcContextElement>
        get() = GrpcContextElement

    override fun restoreThreadContext(context: CoroutineContext, oldState: GrpcContext) {
        grpcContext.detach(oldState)
    }

    override fun updateThreadContext(context: CoroutineContext): GrpcContext {
        return grpcContext.attach()
    }
}
