/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.RPC
import kotlinx.rpc.RPCClient
import kotlin.reflect.KType

@InternalRPCApi
public interface RPCStubObject<T : RPC> :
    RPCStubServiceProvider<T>,
    RPCServiceMethodSerializationTypeProvider,
    RPCServiceFieldsProvider<T>

@InternalRPCApi
public interface RPCStubServiceProvider<T : RPC> {
    public fun withClient(serviceId: Long, client: RPCClient) : T
}

@InternalRPCApi
public interface RPCServiceMethodSerializationTypeProvider {
    public fun methodTypeOf(methodName: String): KType?
}

@InternalRPCApi
public interface RPCServiceFieldsProvider<T : RPC> {
    public fun rpcFields(service: T): List<RPCDeferredField<*>>
}
