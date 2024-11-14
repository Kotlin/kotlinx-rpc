/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST", "unused")
object TestRpcClient : RpcClient {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun <T> call(call: RpcCall): T {
        return "call_42" as T
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("detekt.GlobalCoroutineUsage")
    override fun <T> callAsync(serviceScope: CoroutineScope, call: RpcCall): Deferred<T> {
        val callable = call.descriptor.getCallable(call.callableName)
            ?: error("No callable found for ${call.callableName}")

        val value = when (callable.name) {
            "plainFlow" -> flow { emit("registerPlainFlowField_42") }

            "sharedFlow" -> MutableSharedFlow<String>(1).also {
                GlobalScope.launch { it.emit("registerSharedFlowField_42") }
            }

            "stateFlow" -> MutableStateFlow("registerStateFlowField_42")

            else -> error("Unknown callable name: ${call.callableName}")
        }

        return CompletableDeferred(value as T)
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        return coroutineContext
    }
}
