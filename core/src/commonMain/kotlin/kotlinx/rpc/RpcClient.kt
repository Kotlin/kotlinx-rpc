/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Deprecated("Use RpcClient instead", ReplaceWith("RpcClient"), level = DeprecationLevel.ERROR)
public typealias RPCClient = RpcClient

/**
 * [RpcClient] represents an abstraction of an RPC client, that can handle requests from several RPC services,
 * transform them, send to the server and handle responses and errors.
 * [CoroutineScope] defines the lifetime of the client.
 */
public interface RpcClient {
    /**
     * This method is used by generated clients to perform a call to the server.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return actual result of the call, for example, data from the server.
     */
    public suspend fun <T> call(call: RpcCall): T

    /**
     * This method is used by generated clients to perform a call to the server
     * that returns a streaming flow.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return the actual result of the call, for example, data from the server
     */
    public fun <T> callServerStreaming(call: RpcCall): Flow<T>
}
