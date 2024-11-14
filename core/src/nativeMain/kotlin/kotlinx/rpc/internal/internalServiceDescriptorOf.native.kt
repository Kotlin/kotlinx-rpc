/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import kotlinx.rpc.RemoteService
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@InternalRPCApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(AnnotationTarget.CLASS)
public annotation class WithServiceDescriptor(
    @Suppress("unused")
    val stub: KClass<out RpcServiceDescriptor<out RemoteService>>,
)

@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <T : RemoteService> internalServiceDescriptorOf(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObject<WithServiceDescriptor>()
}
