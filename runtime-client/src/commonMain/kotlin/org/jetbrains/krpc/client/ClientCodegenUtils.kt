package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.RPCStubCall
import kotlin.reflect.KType

@RPCStubCall
@Suppress("UNUSED_PARAMETER")
inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T {
    error("Stub rpcServiceOf function, will be replaced with the module specific version in compile time")
}

@RPCStubCall
@Suppress("UNUSED_PARAMETER")
fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCEngine): T {
    error("Stub rpcServiceOf function, will be replaced with the module specific version in compile time")
}
