/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import kotlinx.rpc.RPC
import kotlinx.rpc.internal.utils.InternalRPCApi
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

@OptIn(ExperimentalAssociatedObjects::class)
@InternalRPCApi
public actual fun <R : Any> findRPCStubProvider(kClass: KClass<*>, resultKClass: KClass<R>): R {
    val associatedObject = kClass.findAssociatedObject<WithRPCStubObject>()
        ?: internalError("Unable to find $kClass associated object")

    if (resultKClass.isInstance(associatedObject)) {
        @Suppress("UNCHECKED_CAST")
        return associatedObject as R
    }

    internalError(
        "Located associated object is not of desired type $resultKClass, " +
                "instead found $associatedObject of class " +
                (associatedObject::class.qualifiedClassNameOrNull ?: associatedObject::class)
    )
}
