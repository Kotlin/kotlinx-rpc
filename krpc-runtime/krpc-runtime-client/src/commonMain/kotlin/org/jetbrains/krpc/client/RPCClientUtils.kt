/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.internal.RPCClientProvider
import org.jetbrains.krpc.internal.findRPCProviderInCompanion
import org.jetbrains.krpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

inline fun <reified T : RPC> RPCClient.withService(): T {
    return withService(T::class)
}

fun <T : RPC> RPCClient.withService(kType: KType): T {
    return withService(kType.kClass())
}

fun <T : RPC> RPCClient.withService(serviceKClass: KClass<T>): T {
    val withRPCClientObject = findRPCProviderInCompanion<RPCClientProvider<T>>(serviceKClass)
    return withRPCClientObject.withClient(this)
}
