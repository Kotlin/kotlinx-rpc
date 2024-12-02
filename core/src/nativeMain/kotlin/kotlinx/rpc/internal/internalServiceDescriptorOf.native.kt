/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@InternalRpcApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(AnnotationTarget.CLASS)
public annotation class WithServiceDescriptor(
    @Suppress("unused")
    val stub: KClass<out RpcServiceDescriptor<*>>,
)

@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <@Rpc T : Any> internalServiceDescriptorOf(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObject<WithServiceDescriptor>()
}
