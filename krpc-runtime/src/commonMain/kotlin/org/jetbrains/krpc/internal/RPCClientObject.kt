package org.jetbrains.krpc.internal

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCClientEngine
import kotlin.reflect.KType

@InternalKRPCApi
interface RPCClientObject<T : RPC> : RPCClientProvider<T>, RPCServiceMethodSerializationTypeProvider, RPCServiceFieldsProvider<T>

@InternalKRPCApi
interface RPCClientProvider<T : RPC> {
    fun client(engine: RPCClientEngine) : T
}

@InternalKRPCApi
interface RPCServiceMethodSerializationTypeProvider {
    fun methodTypeOf(methodName: String): KType?
}

@InternalKRPCApi
interface RPCServiceFieldsProvider<T : RPC> {
    fun rpcFields(client: T): List<RPCField<*>>
}
