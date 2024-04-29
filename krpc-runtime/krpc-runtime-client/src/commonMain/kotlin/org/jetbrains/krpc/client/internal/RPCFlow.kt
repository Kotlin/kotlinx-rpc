/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.krpc.internal.RPCDeferredField
import org.jetbrains.krpc.internal.SupervisedCompletableDeferred

internal sealed class RPCFlow<T, FlowT : Flow<T>>(private val serviceName: String, parent: Job) :
    RPCDeferredField<FlowT> {
    val deferred: CompletableDeferred<FlowT> = SupervisedCompletableDeferred(parent)

    override suspend fun await(): FlowT {
        return deferred.await()
    }

    internal class Plain<T>(serviceName: String, parent: Job) : RPCFlow<T, Flow<T>>(serviceName, parent),
        Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            deferred.await().collect(collector)
        }
    }

    internal class Shared<T>(serviceName: String, parent: Job) : RPCFlow<T, SharedFlow<T>>(serviceName, parent),
        SharedFlow<T> {
        override val replayCache: List<T> by rpcProperty { replayCache }

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            deferred.await().collect(collector)
        }
    }

    internal class State<T>(serviceName: String, parent: Job) : RPCFlow<T, StateFlow<T>>(serviceName, parent),
        StateFlow<T> {
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
