/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc.internal

import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

@InternalRPCApi
public actual fun <R : Any> findRPCStubProvider(kClass: KClass<*>, resultKClass: KClass<R>): R {
    val candidate = kClass.java.classLoader
        .loadClass("${kClass.qualifiedName}\$\$rpcServiceStub")
        ?.kotlin
        ?.companionObjectInstance

    @Suppress("UNCHECKED_CAST")
    if (resultKClass.isInstance(candidate)) {
        return candidate as R
    }

    internalError("unable to find $kClass rpc client object")
}
