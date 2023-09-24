package org.jetbrains.krpc

import org.jetbrains.krpc.client.rpcServiceOf
import org.jetbrains.krpc.server.rpcServiceMethodSerializationTypeOf
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.typeOf

interface EmptyService {
    suspend fun empty()
}

val stubEngine = object : RPCEngine {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

    override suspend fun call(callInfo: RPCCallInfo): Any? {
        println("Called ${callInfo.methodName}")
        return null
    }
}

interface CommonService : RPC, EmptyService {
    override suspend fun empty()
}

inline fun <reified T : RPC> testService(test: T.() -> Unit) {
    rpcServiceOf<T>(stubEngine).test()

    rpcServiceOf<T>(typeOf<T>(), stubEngine).test()

    println(rpcServiceMethodSerializationTypeOf<T>("empty"))
    println(rpcServiceMethodSerializationTypeOf(typeOf<T>(),"empty"))
}
