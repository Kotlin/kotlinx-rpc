/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.internal.RpcFlow

/**
 * Registers Flow<T> field of the interface. Sends initialization request, subscribes to emitted values
 * and returns the instance of the flow to be consumed
 *
 * @param T type parameter for Flow
 * @param serviceScope Service's coroutine scope
 * @param descriptor descriptor of the service, that made the call
 * that is used to be mapped to the corresponding field on a server.
 * @param fieldName the name of the field.
 * @param serviceId id of the service, that made the call
 * @return Flow instance to be consumed.
 */
@Deprecated(
    "Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
    level = DeprecationLevel.ERROR,
)
public fun <T> RpcClient.registerPlainFlowField(
    serviceScope: CoroutineScope,
    descriptor: RpcServiceDescriptor<*>,
    fieldName: String,
    serviceId: Long,
): Flow<T> {
    return RpcFlow.Plain(descriptor.fqName, initializeFlowField(serviceScope, descriptor, fieldName, serviceId))
}

/**
 * Registers SharedFlow<T> field of the interface. Sends initialization request, subscribes to emitted values
 * and returns the instance of the flow to be consumed
 *
 * @param T type parameter for SharedFlow
 * @param serviceScope Service's coroutine scope
 * @param descriptor descriptor of the service, that made the call
 * that is used to be mapped to the corresponding field on a server.
 * @param fieldName the name of the field.
 * @param serviceId id of the service, that made the call
 * @return SharedFlow instance to be consumed.
 */
@Deprecated(
    "Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
    level = DeprecationLevel.ERROR,
)
public fun <T> RpcClient.registerSharedFlowField(
    serviceScope: CoroutineScope,
    descriptor: RpcServiceDescriptor<*>,
    fieldName: String,
    serviceId: Long,
): SharedFlow<T> {
    return RpcFlow.Shared(descriptor.fqName, initializeFlowField(serviceScope, descriptor, fieldName, serviceId))
}

/**
 * Registers StateFlow<T> field of the interface. Sends initialization request, subscribes to emitted values
 * and returns the instance of the flow to be consumed
 *
 * @param T type parameter for StateFlow
 * @param serviceScope Service's coroutine scope
 * @param descriptor descriptor of the service, that made the call
 * that is used to be mapped to the corresponding field on a server.
 * @param fieldName the name of the field.
 * @param serviceId id of the service, that made the call
 * @return StateFlow instance to be consumed.
 */
@Deprecated(
    "Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
    level = DeprecationLevel.ERROR,
)
public fun <T> RpcClient.registerStateFlowField(
    serviceScope: CoroutineScope,
    descriptor: RpcServiceDescriptor<*>,
    fieldName: String,
    serviceId: Long,
): StateFlow<T> {
    return RpcFlow.State(descriptor.fqName, initializeFlowField(serviceScope, descriptor, fieldName, serviceId))
}

@Suppress("unused", "UnusedReceiverParameter")
private fun <T, FlowT : Flow<T>> RpcClient.initializeFlowField(
    serviceScope: CoroutineScope,
    descriptor: RpcServiceDescriptor<*>,
    fieldName: String,
    serviceId: Long,
): Deferred<FlowT> {
    error("Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html")
}
