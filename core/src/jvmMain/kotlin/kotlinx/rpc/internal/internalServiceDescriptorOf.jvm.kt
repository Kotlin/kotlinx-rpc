/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.RpcService
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@InternalRpcApi
@Target(AnnotationTarget.CLASS)
public actual annotation class WithServiceDescriptor(
    actual val stub: KClass<out RpcServiceDescriptor<*, *>>,
)

internal actual fun <Service : RpcService<*, Service>> internalServiceDescriptorOf(kClass: KClass<Service>): Any? {
    val stubClass = kClass.findAnnotation<WithServiceDescriptor>()?.stub ?: return null
    return stubClass.objectInstance
}
