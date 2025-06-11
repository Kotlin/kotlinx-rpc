/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
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

    override fun <T> callServerStreaming(call: RpcCall): Flow<T> {
        return flow { emit("stream_42" as T) }
    }
}
