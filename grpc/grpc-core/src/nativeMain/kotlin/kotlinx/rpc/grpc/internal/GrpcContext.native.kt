/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlin.coroutines.CoroutineContext

internal actual class GrpcContext

internal actual val CurrentGrpcContext: GrpcContext
    get() = TODO("Not yet implemented")

internal actual class GrpcContextElement : CoroutineContext.Element {
    actual override val key: CoroutineContext.Key<GrpcContextElement>
        get() = TODO("Not yet implemented")

    actual companion object Key : CoroutineContext.Key<GrpcContextElement> {
        actual fun current(): GrpcContextElement {
            TODO("Not yet implemented")
        }
    }
}
