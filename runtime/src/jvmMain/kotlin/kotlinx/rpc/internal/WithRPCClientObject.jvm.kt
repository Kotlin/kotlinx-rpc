/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc.internal

import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

@InternalRPCApi
public actual fun <R> findRPCProviderInCompanion(kClass: KClass<*>): R {
    @Suppress("UNCHECKED_CAST")
    return kClass.java.classLoader
        .loadClass("kotlinx.rpc.${kClass.simpleName}Client")
        ?.kotlin
        ?.companionObjectInstance as? R
        ?: internalError("unable to find $kClass rpc client object")
}
