package org.jetbrains.krpc

import kotlin.reflect.KType

// markers for compiler plugin
annotation class RPCStubCall
annotation class RPCReplacementCall

@Suppress("UNUSED_PARAMETER")
@RPCStubCall
inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T {
    error("Stub rpcServiceOf function, will be replaced with call to a companion object in compile time")
}

@RPCReplacementCall
@Suppress("unused")
@Deprecated("For internal use only", level = DeprecationLevel.HIDDEN)
fun <T : RPC> rpcServiceOf(serviceProvider: RPCClientProvider<T>, engine: RPCEngine): T {
    return serviceProvider.client(engine)
}

@RPCStubCall
@Suppress("UNUSED_PARAMETER", "unused")
inline fun <reified T : RPC> serviceMethodOf(methodName: String): KType? {
    error("Stub serviceMethodOf function, will be replaced with call to a companion object in compile time")
}

@RPCReplacementCall
@Suppress("unused")
@Deprecated("For internal use only", level = DeprecationLevel.HIDDEN)
fun serviceMethodOf(methodInfoProvider: RPCMethodClassTypeProvider, methodName: String): KType? {
    return methodInfoProvider.methodClassType(methodName)
}
