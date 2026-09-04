/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual abstract class ClientCall<Request, Response> {
    public actual abstract fun start(responseListener: Listener<Response>, headers: GrpcMetadata)
    public actual abstract fun request(numMessages: Int)
    public actual abstract fun cancel(message: String?, cause: Throwable?)
    public actual abstract fun halfClose()
    public actual abstract fun sendMessage(message: Request)
    public actual open fun isReady(): Boolean = TODO("Implement the iOS gRPC client")

    @InternalRpcApi
    public actual abstract class Listener<Message> {
        public actual open fun onHeaders(headers: GrpcMetadata): Unit = TODO("Implement the iOS gRPC client")
        public actual open fun onMessage(message: Message): Unit = TODO("Implement the iOS gRPC client")
        public actual open fun onClose(status: GrpcStatus, trailers: GrpcMetadata): Unit =
            TODO("Implement the iOS gRPC client")
        public actual open fun onReady(): Unit = TODO("Implement the iOS gRPC client")
    }
}

@InternalRpcApi
public actual fun <Message> clientCallListener(
    onHeaders: (headers: GrpcMetadata) -> Unit,
    onMessage: (message: Message) -> Unit,
    onClose: (status: GrpcStatus, trailers: GrpcMetadata) -> Unit,
    onReady: () -> Unit,
): ClientCall.Listener<Message> = TODO("Implement the iOS gRPC client")
