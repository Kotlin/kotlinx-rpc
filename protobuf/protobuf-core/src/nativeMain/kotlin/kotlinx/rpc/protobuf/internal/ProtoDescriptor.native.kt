/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.WithServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.GeneratedProtoMessage
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@InternalRpcApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(AnnotationTarget.CLASS)
public actual annotation class WithProtoDescriptor(
    @Suppress("unused")
    actual val descriptor: KClass<out ProtoDescriptor<*>>,
)

@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <@GeneratedProtoMessage T : Any> findProtoDescriptorOf(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObject<WithServiceDescriptor>()
}