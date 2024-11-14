/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal sealed class RpcFlow<T, FlowT : Flow<T>>(
    private val serviceName: String,
    protected val deferred: Deferred<FlowT>,
) : RpcDeferredField<FlowT> {
    override suspend fun await(): FlowT {
        return deferred.await()
    }

    internal class Plain<T>(
        serviceName: String,
        deferred: Deferred<Flow<T>>,
    ) : RpcFlow<T, Flow<T>>(serviceName, deferred), Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            deferred.await().collect(collector)
        }
    }

    internal class Shared<T>(
        serviceName: String,
        deferred: Deferred<SharedFlow<T>>,
    ) : RpcFlow<T, SharedFlow<T>>(serviceName, deferred), SharedFlow<T> {
        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    internal class State<T>(
        serviceName: String,
        deferred: Deferred<StateFlow<T>>,
    ) : RpcFlow<T, StateFlow<T>>(serviceName, deferred), StateFlow<T> {
        override val value: T by rpcProperty { value }

        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    protected fun <R> rpcProperty(getter: FlowT.() -> R): RpcFieldProvider<FlowT, R> {
        return RpcFieldProvider(serviceName, deferred, getter)
    }
}
