/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation

@InternalRpcApi
@Target(allowedTargets = [AnnotationTarget.CLASS])
public actual annotation class WithProtoDescriptor(
    actual val descriptor: KClass<out ProtoDescriptor<*>>
)

internal actual fun <@kotlinx.rpc.protobuf.ProtoMessage T : Any> findProtoDescriptorOf(kClass: KClass<T>): Any? {
    val descriptorClass = kClass.findAnnotation<WithProtoDescriptor>()?.descriptor ?: return null
    return descriptorClass.objectInstance
}
