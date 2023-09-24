package org.jetbrains.krpc.internal

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import kotlin.reflect.KType

@InternalKRPCApi
interface RPCClientObject<T : RPC> : RPCClientProvider<T>, RPCServiceMethodSerializationTypeProvider

@InternalKRPCApi
interface RPCClientProvider<T : RPC> {
    fun client(engine: RPCEngine) : T
}

@InternalKRPCApi
interface RPCServiceMethodSerializationTypeProvider {
    fun methodTypeOf(methodName: String): KType?
}
