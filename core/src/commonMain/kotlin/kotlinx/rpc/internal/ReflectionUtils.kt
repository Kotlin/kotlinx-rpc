/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@InternalRpcApi
@Suppress("UNCHECKED_CAST")
public fun <T : Any> KType.rpcInternalKClass(): KClass<T> {
    val classifier = classifier ?: error("Expected denotable type, found $this")
    val classifierClass = classifier as? KClass<*> ?: error("Expected class type, found $this")

    return classifierClass as KClass<T>
}

@InternalRpcApi
public fun internalRpcError(message: String): Nothing {
    error("Internal kotlinx.rpc error: $message")
}

@InternalRpcApi
public expect val KClass<*>.rpcInternalTypeName: String?

@InternalRpcApi
public expect val KClass<*>.rpcInternalQualifiedClassNameOrNull: String?

@InternalRpcApi
public val KClass<*>.rpcInternalQualifiedClassName: String  get() = rpcInternalQualifiedClassNameOrNull
    ?: error("Expected qualifiedClassName for $this")
