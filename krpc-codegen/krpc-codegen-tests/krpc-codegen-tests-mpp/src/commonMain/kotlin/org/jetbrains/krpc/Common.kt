package org.jetbrains.krpc

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.krpc.client.clientOf
import org.jetbrains.krpc.server.rpcServiceMethodSerializationTypeOf
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface EmptyService {
    suspend fun empty()
}

val stubEngine = object : RPCEngine {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun call(callInfo: RPCCallInfo, deferred: CompletableDeferred<*>): Any? {
        println("Called ${callInfo.callableName}")
        return null
    }

    override fun <T> registerFlowField(fieldName: String, type: KType): Flow<T> {
        TODO("Not yet implemented")
    }

    override fun <T> registerSharedFlowField(fieldName: String, type: KType): SharedFlow<T> {
        TODO("Not yet implemented")
    }

    override fun <T> registerStateFlowField(fieldName: String, type: KType): StateFlow<T> {
        TODO("Not yet implemented")
    }
}

interface CommonService : RPC, EmptyService {
    override suspend fun empty()
}

inline fun <reified T : RPC> testService(test: T.() -> Unit) {
    RPC.clientOf<T>(stubEngine).test()
    RPC.clientOf<T>(typeOf<T>(), stubEngine).test()

    println(rpcServiceMethodSerializationTypeOf<T>("empty"))
    println(rpcServiceMethodSerializationTypeOf(typeOf<T>(), "empty"))
}
