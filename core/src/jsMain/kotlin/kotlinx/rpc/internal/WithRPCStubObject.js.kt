/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import js.objects.Object
import kotlinx.rpc.RPC
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@InternalRPCApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(AnnotationTarget.CLASS)
public annotation class WithRPCStubObject(
    @Suppress("unused")
    val stub: KClass<out RPCStubObject<out RPC>>
)

@InternalRPCApi
public actual fun <R : Any> findRPCStubProvider(kClass: KClass<*>, resultKClass: KClass<R>): R {
    val associatedObject = kClass.findAssociatedObjectImpl(WithRPCStubObject::class, resultKClass)
        ?: internalError("Unable to find $kClass associated object")

    if (resultKClass.isInstance(associatedObject)) {
        @Suppress("UNCHECKED_CAST")
        return associatedObject as R
    }

    internalError(
        "Located associated object is not of desired type $resultKClass, " +
                "instead found $associatedObject of class " +
                (associatedObject::class.qualifiedClassNameOrNull ?: associatedObject::class.js.name)
    )
}

/**
 * [KClassImpl] is internal in kjs stdlib, and it's jClass property is obfuscated by the webpack.
 * Hence, we need to find it manually.
 */
private val KClass<*>.jClass get(): JsClass<*> {
    return Object.entries(this)
        // key (_) is obfuscated here
        .firstOrNull { (_, value) -> value != null && Object.hasOwn(value,"\$metadata\$") }
        ?.component2()
        ?.unsafeCast<JsClass<*>>()
        ?: error("jClass property was not found")
}

/**
 * Workaround for bugs in [findAssociatedObject]
 * See KT-70132 for more info.
 *
 * This function uses std-lib's implementation and accounts for the bug in the compiler
 */
internal fun <T : Annotation, R : Any> KClass<*>.findAssociatedObjectImpl(
    annotationClass: KClass<T>,
    resultKClass: KClass<R>,
): Any? {
    val key = annotationClass.jClass.asDynamic().`$metadata$`?.associatedObjectKey?.unsafeCast<Int>() ?: return null
    val map = jClass.asDynamic().`$metadata$`?.associatedObjects ?: return null
    val factory = map[key] ?: return fallbackFindAssociatedObjectImpl(map, resultKClass)
    return factory()
}

private fun <R : Any> fallbackFindAssociatedObjectImpl(map: dynamic, resultKClass: KClass<R>): R? {
    return Object.entries(map as Any)
        .mapNotNull { (_, factory) ->
            val unsafeFactory = factory.asDynamic()
            val maybeObject = unsafeFactory()
            if (resultKClass.isInstance(maybeObject)) {
                maybeObject.unsafeCast<R>()
            } else {
                null
            }
        }
        .singleOrNull()
}
