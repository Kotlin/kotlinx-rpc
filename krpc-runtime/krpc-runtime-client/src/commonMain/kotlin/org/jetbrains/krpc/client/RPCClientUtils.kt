/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client

import kotlinx.atomicfu.atomic
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.internal.RPCClientProvider
import org.jetbrains.krpc.internal.findRPCProviderInCompanion
import org.jetbrains.krpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Creates instance of the generated service [T], that is able to communicate with server using RPCClient.
 *
 * [awaitFieldInitialization] method can be used on that instance.
 *
 * @param T exact type of the service to be created.
 * @return instance of the generated service.
 */
public inline fun <reified T : RPC> RPCClient.withService(): T {
    return withService(T::class)
}

/**
 * Creates instance of the generated service [T], that is able to communicate with server using RPCClient.
 *
 * [awaitFieldInitialization] method can be used on that instance.
 *
 * @param T exact type of the service to be created.
 * @param serviceKType [KType] of the service to be created.
 * @return instance of the generated service.
 */
public fun <T : RPC> RPCClient.withService(serviceKType: KType): T {
    return withService(serviceKType.kClass())
}

/**
 * Counter for locally added services.
 * Used to differentiate uniques local services, regardless of their type.
 */
private val SERVICE_ID = atomic(0L)

/**
 * Creates instance of the generated service [T], that is able to communicate with server using RPCClient.
 *
 * [awaitFieldInitialization] method can be used on that instance.
 *
 * @param T exact type of the service to be created.
 * @param serviceKClass [KClass] of the service to be created.
 * @return instance of the generated service.
 */
public fun <T : RPC> RPCClient.withService(serviceKClass: KClass<T>): T {
    val provider = findRPCProviderInCompanion<RPCClientProvider<T>>(serviceKClass)
    val id = SERVICE_ID.incrementAndGet()

    return provider.withClient(id, this)
}
