package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.RPCClientProvider
import org.jetbrains.krpc.internal.kClass
import org.jetbrains.krpc.internal.withRPCClientObject
import kotlin.reflect.KClass
import kotlin.reflect.KType

actual inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T {
    return rpcServiceOf(T::class, engine)
}

actual fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCEngine): T {
    return rpcServiceOf(serviceType.kClass(), engine)
}

@OptIn(InternalKRPCApi::class)
fun <T : RPC> rpcServiceOf(kClass: KClass<T>, engine: RPCEngine): T {
    return withRPCClientObject<RPCClientProvider<T>>(kClass).client(engine)
}
