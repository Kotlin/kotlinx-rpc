/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RPCCall
import kotlinx.rpc.RPCClient
import kotlinx.rpc.RPCField
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST", "unused")
object TestRpcClient : RPCClient {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun <T> call(call: RPCCall): T {
        return "call_42" as T
    }

    override fun <T> registerPlainFlowField(serviceScope: CoroutineScope, field: RPCField): Flow<T> {
        return flow { emit("registerPlainFlowField_42") } as Flow<T>
    }

    override fun <T> registerSharedFlowField(serviceScope: CoroutineScope, field: RPCField): SharedFlow<T> {
        return MutableStateFlow("registerSharedFlowField_42") as SharedFlow<T>
    }

    override fun <T> registerStateFlowField(serviceScope: CoroutineScope, field: RPCField): StateFlow<T> {
        return MutableStateFlow("registerStateFlowField_42") as StateFlow<T>
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        return coroutineContext
    }
}
