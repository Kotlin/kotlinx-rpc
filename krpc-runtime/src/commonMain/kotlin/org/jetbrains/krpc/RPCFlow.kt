package org.jetbrains.krpc

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.krpc.internal.InternalKRPCApi

@OptIn(InternalKRPCApi::class)
sealed class RPCFlow<T, FlowT : Flow<T>>(private val serviceName: String): RPCProperty<FlowT> {
    val deferred: CompletableDeferred<FlowT> = CompletableDeferred()

    override suspend fun awaitField(): FlowT {
        return deferred.await()
    }

    class Plain<T>(serviceName: String) : RPCFlow<T, Flow<T>>(serviceName), Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            deferred.await().collect(collector)
        }
    }

    class Shared<T>(serviceName: String) : RPCFlow<T, SharedFlow<T>>(serviceName), SharedFlow<T> {
        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    class State<T>(serviceName: String): RPCFlow<T, StateFlow<T>>(serviceName), StateFlow<T> {
        override val value: T by rpcProperty { value }

        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    protected fun <R> rpcProperty(getter: FlowT.() -> R): RPCPropertyProvider<FlowT, R> {
        return RPCPropertyProvider(serviceName, deferred, getter)
    }
}
