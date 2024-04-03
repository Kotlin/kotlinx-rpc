/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package org.jetbrains.krpc.internal

import org.jetbrains.krpc.RPC
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@InternalKRPCApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(AnnotationTarget.CLASS)
public annotation class WithRPCStubObject(
    @Suppress("unused")
    val stub: KClass<out RPCStubObject<out RPC>>
)

@InternalKRPCApi
@OptIn(ExperimentalAssociatedObjects::class)
public actual fun <R> findRPCProviderInCompanion(kClass: KClass<*>): R {
    @Suppress("UNCHECKED_CAST")
    return kClass.findAssociatedObject<WithRPCStubObject>() as? R
        ?: internalError("unable to find $kClass rpc stub object")
}
