package org.jetbrains.krpc

import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface RPC

public object KRPC {
    val RPC_SERVICES = mutableMapOf<KType, (RPCEngine) -> RPC>()
}

inline fun <reified T> rpcClientOf(engine: RPCEngine): T {
    return rpcClientOf(typeOf<T>(), engine)
}

fun <T> rpcClientOf(kType: KType, engine: RPCEngine): T {
    return KRPC.RPC_SERVICES[kType]!!.invoke(engine) as T
}
