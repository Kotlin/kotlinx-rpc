/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@ExperimentalRpcApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(allowedTargets = [AnnotationTarget.CLASS])
public actual annotation class WithMarshaller actual constructor(
    actual val marshaller: KClass<out MessageMarshaller<*>>
)

@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <T : Any> resolveMarshaller(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObject<WithMarshaller>()
}
