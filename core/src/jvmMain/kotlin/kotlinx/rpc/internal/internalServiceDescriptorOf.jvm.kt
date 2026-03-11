/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@InternalRpcApi
@Target(AnnotationTarget.CLASS)
public actual annotation class WithServiceDescriptor(
    actual val stub: KClass<out RpcServiceDescriptor<*>>,
)

internal actual fun <@Rpc T : Any> internalServiceDescriptorOf(kClass: KClass<T>): Any? {
    val stubClass = kClass.findAnnotation<WithServiceDescriptor>()?.stub ?: return null
    return stubClass.objectInstance
}
