package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.RPCStubCall

@Suppress("UNUSED_PARAMETER")
@RPCStubCall
inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T {
    error("Stub rpcServiceOf function, will be replaced with call to a companion object in compile time")
}
