/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@ExperimentalRpcApi
@Target(allowedTargets = [AnnotationTarget.CLASS])
public actual annotation class WithMarshaller actual constructor(
    actual val marshaller: KClass<out MessageMarshaller<*>>
)

internal actual fun <T : Any> resolveMarshaller(kClass: KClass<T>): Any? {
    val marshallerClass = kClass.findAnnotation<WithMarshaller>()?.marshaller ?: return null
    return marshallerClass.objectInstance
}
