/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.RPC
import kotlinx.rpc.RPCClient
import kotlin.reflect.KType

@InternalKRPCApi
public interface RPCClientObject<T : RPC> :
    RPCClientProvider<T>,
    RPCServiceMethodSerializationTypeProvider,
    RPCServiceFieldsProvider<T>

@InternalKRPCApi
public interface RPCClientProvider<T : RPC> {
    public fun withClient(serviceId: Long, client: RPCClient) : T
}

@InternalKRPCApi
public interface RPCServiceMethodSerializationTypeProvider {
    public fun methodTypeOf(methodName: String): KType?
}

@InternalKRPCApi
public interface RPCServiceFieldsProvider<T : RPC> {
    public fun rpcFields(service: T): List<RPCDeferredField<*>>
}
