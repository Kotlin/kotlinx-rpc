package org.jetbrains.krpc

interface RPCEngine {
    suspend fun register(methodInfo: RPCMethodInfo)

    suspend fun call(callInfo: RPCCallInfo): Any
}
