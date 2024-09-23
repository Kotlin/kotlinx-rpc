/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@InternalRPCApi
@Suppress("UNCHECKED_CAST")
public fun <T : Any> KType.kClass(): KClass<T> {
    val classifier = classifier ?: error("Expected denotable type, found $this")
    val classifierClass = classifier as? KClass<*> ?: error("Expected class type, found $this")

    return classifierClass as KClass<T>
}

@InternalRPCApi
public fun internalError(message: String): Nothing {
    error("Internal kotlinx.rpc error: $message")
}

@InternalRPCApi
public expect val KClass<*>.typeName: String?

@InternalRPCApi
public expect val KClass<*>.qualifiedClassNameOrNull: String?

@InternalRPCApi
public val KClass<*>.qualifiedClassName: String  get() = qualifiedClassNameOrNull
    ?: error("Expected qualifiedClassName for $this")
