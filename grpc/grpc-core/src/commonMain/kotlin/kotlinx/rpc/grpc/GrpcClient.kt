/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.rpc.RPCCall
import kotlinx.rpc.RPCClient
import kotlinx.rpc.RPCField
import kotlin.coroutines.CoroutineContext

public class GrpcClient : RPCClient {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

    override suspend fun <T> call(call: RPCCall): T {
        TODO("Not yet implemented")
    }

    override fun <T> registerPlainFlowField(serviceScope: CoroutineScope, field: RPCField): Flow<T> {
        error("gRPC client does not support field declarations")
    }

    override fun <T> registerSharedFlowField(serviceScope: CoroutineScope, field: RPCField): SharedFlow<T> {
        error("gRPC client does not support field declarations")
    }

    override fun <T> registerStateFlowField(serviceScope: CoroutineScope, field: RPCField): StateFlow<T> {
        error("gRPC client does not support field declarations")
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        TODO("Not yet implemented")
    }
}
