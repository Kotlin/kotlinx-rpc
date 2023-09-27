package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.RPCTransport
import kotlin.reflect.KType

inline fun <reified T : RPC> rpcServiceOf(engine: RPCTransport): T {
    val engine = RPCClientEngine(engine)
    return rpcServiceOf<T>(engine)
}

expect inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T

expect fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCEngine): T
