/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.krpc.internal.RPCDeferredField

internal sealed class RPCFlow<T, FlowT : Flow<T>>(private val serviceName: String): RPCDeferredField<FlowT> {
    val deferred: CompletableDeferred<FlowT> = CompletableDeferred()

    override suspend fun await(): FlowT {
        return deferred.await()
    }

    internal class Plain<T>(serviceName: String) : RPCFlow<T, Flow<T>>(serviceName), Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            deferred.await().collect(collector)
        }
    }

    internal class Shared<T>(serviceName: String) : RPCFlow<T, SharedFlow<T>>(serviceName), SharedFlow<T> {
        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    internal class State<T>(serviceName: String): RPCFlow<T, StateFlow<T>>(serviceName), StateFlow<T> {
        override val value: T by rpcProperty { value }

        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    protected fun <R> rpcProperty(getter: FlowT.() -> R): RPCFieldProvider<FlowT, R> {
        return RPCFieldProvider(serviceName, deferred, getter)
    }
}
