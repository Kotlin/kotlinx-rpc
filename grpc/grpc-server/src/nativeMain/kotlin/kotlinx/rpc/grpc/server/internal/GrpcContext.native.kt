/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlin.coroutines.CoroutineContext

public actual class GrpcContext

private val currentGrpcContext = GrpcContext()

internal actual val CurrentGrpcContext: GrpcContext
    get() = currentGrpcContext

internal actual class GrpcContextElement(actual val grpcContext: GrpcContext) : CoroutineContext.Element {
    actual override val key: CoroutineContext.Key<GrpcContextElement>
        get() = Key

    actual companion object Key : CoroutineContext.Key<GrpcContextElement> {
        actual fun current(): GrpcContextElement {
            return GrpcContextElement(currentGrpcContext)
        }
    }
}
