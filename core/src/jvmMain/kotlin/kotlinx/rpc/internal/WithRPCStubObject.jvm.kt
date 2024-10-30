/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc.internal

import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

private const val RPC_SERVICE_STUB_SIMPLE_NAME = "\$rpcServiceStub"

@InternalRPCApi
public actual fun <R : Any> findRPCStubProvider(kClass: KClass<*>, resultKClass: KClass<R>): R {
    val className = when {
        KotlinVersion.CURRENT.isAtLeast(2, 0) -> {
            "${kClass.qualifiedName}\$$RPC_SERVICE_STUB_SIMPLE_NAME"
        }

        else -> {
            error("kotlinx.rpc runtime is not compatible with Kotlin version prior to 2.0")
        }
    }

    val candidate = kClass.java.classLoader
        .loadClass(className)
        ?.kotlin
        ?.companionObjectInstance

    @Suppress("UNCHECKED_CAST")
    if (resultKClass.isInstance(candidate)) {
        return candidate as R
    }

    internalError("unable to find $kClass rpc client object")
}
