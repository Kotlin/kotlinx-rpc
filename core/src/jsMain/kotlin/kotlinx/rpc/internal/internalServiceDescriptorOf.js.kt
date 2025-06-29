/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import js.array.component1
import js.array.component2
import js.objects.Object
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

internal actual fun <@Rpc T : Any> internalServiceDescriptorOf(kClass: KClass<T>): Any? {
    return kClass.findAssociatedObjectImpl(WithServiceDescriptor::class)
}

/**
 * Workaround for bugs in [findAssociatedObject]
 * See KT-70132 for more info.
 *
 * This function uses std-lib's implementation and accounts for the bug in the compiler
 */
internal fun KClass<*>.findAssociatedObjectImpl(annotationClass: KClass<*>): Any? {
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
