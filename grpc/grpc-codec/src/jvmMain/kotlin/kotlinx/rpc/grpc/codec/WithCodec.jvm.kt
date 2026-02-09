/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@ExperimentalRpcApi
@Target(allowedTargets = [AnnotationTarget.CLASS])
public actual annotation class WithCodec actual constructor(
    actual val codec: KClass<out MessageCodec<*>>
)

internal actual fun <T : Any> resolveCodec(kClass: KClass<T>): Any? {
    val codecClass = kClass.findAnnotation<WithCodec>()?.codec ?: return null
    return codecClass.objectInstance
}