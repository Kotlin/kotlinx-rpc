/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.KrpcProtocol

@Suppress("UNCHECKED_CAST", "unused")
object TestRpcClient : RpcClient<KrpcProtocol>() {
    override suspend fun <T> call(call: RpcCall): T {
        return "call_42" as T
    }

    override fun <T> callStreaming(call: RpcCall): Flow<T> {
        return flow { emit("stream_42" as T) }
    }
}
