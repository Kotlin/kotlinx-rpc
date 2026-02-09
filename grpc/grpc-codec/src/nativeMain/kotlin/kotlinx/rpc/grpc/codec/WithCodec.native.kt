/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@ExperimentalRpcApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(allowedTargets = [AnnotationTarget.CLASS])
public actual annotation class WithCodec actual constructor(
    actual val codec: KClass<out MessageCodec<*>>
)

@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <T : Any> resolveCodec(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObject<WithCodec>()
}