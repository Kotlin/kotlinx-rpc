/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlin.coroutines.CoroutineContext

public expect class GrpcContext

internal expect val CurrentGrpcContext: GrpcContext

internal expect class GrpcContextElement : CoroutineContext.Element {
    val grpcContext: GrpcContext

    companion object Key : CoroutineContext.Key<GrpcContextElement> {
        fun current(): GrpcContextElement
    }

    override val key: CoroutineContext.Key<GrpcContextElement>
}
