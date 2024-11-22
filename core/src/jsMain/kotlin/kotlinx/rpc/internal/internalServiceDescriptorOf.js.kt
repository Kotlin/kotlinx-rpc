/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import js.objects.Object
import kotlinx.rpc.RemoteService
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
    val stub: KClass<out RpcServiceDescriptor<out RemoteService>>,
)

internal actual fun <T : RemoteService> internalServiceDescriptorOf(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObjectImpl(WithServiceDescriptor::class)
}

/**
 * Workaround for bugs in [findAssociatedObject]
 * See KT-70132 for more info.
 *
 * This function uses std-lib's implementation and accounts for the bug in the compiler
 */
internal fun <T : Annotation> KClass<*>.findAssociatedObjectImpl(annotationClass: KClass<T>): Any? {
    val key = annotationClass.js.asDynamic().`$metadata$`?.associatedObjectKey?.unsafeCast<Int>() ?: return null
    val map = js.asDynamic().`$metadata$`?.associatedObjects ?: return null
    val factory = map[key] ?: return fallbackFindAssociatedObjectImpl(map)
    return factory()
}

private fun <R : Any> fallbackFindAssociatedObjectImpl(map: dynamic): R? {
    return Object.entries(map as Any)
        .mapNotNull { (_, factory) ->
            val unsafeFactory = factory.asDynamic()
            val maybeObject = unsafeFactory()
            if (RpcServiceDescriptor::class.isInstance(maybeObject)) {
                maybeObject.unsafeCast<R>()
            } else {
                null
            }
        }
        .singleOrNull()
}
