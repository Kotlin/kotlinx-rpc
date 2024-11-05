/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.atomicfu.atomic
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Creates instance of the generated service [T], that is able to communicate with server using [RpcClient].
 *
 * [awaitFieldInitialization] method can be used on that instance.
 *
 * @param T the exact type of the service to be created.
 * @return instance of the generated service.
 */
public inline fun <reified T : RemoteService> RpcClient.withService(): T {
    return withService(T::class)
}

/**
 * Creates instance of the generated service [T], that is able to communicate with server using [RpcClient].
 *
 * [awaitFieldInitialization] method can be used on that instance.
 *
 * @param T the exact type of the service to be created.
 * @param serviceKType [KType] of the service to be created.
 * @return instance of the generated service.
 */
public fun <T : RemoteService> RpcClient.withService(serviceKType: KType): T {
    return withService(serviceKType.kClass())
}

/**
 * Counter for locally added services.
 * Used to differentiate uniques local services, regardless of their type.
 */
private val SERVICE_ID = atomic(0L)

/**
 * Creates instance of the generated service [T], that is able to communicate with server using [RpcClient].
 *
 * [awaitFieldInitialization] method can be used on that instance.
 *
 * @param T the exact type of the service to be created.
 * @param serviceKClass [KClass] of the service to be created.
 * @return instance of the generated service.
 */
public fun <T : RemoteService> RpcClient.withService(serviceKClass: KClass<T>): T {
    val descriptor = serviceDescriptorOf(serviceKClass)

    val id = SERVICE_ID.incrementAndGet()

    return descriptor.createInstance(id, this)
}
