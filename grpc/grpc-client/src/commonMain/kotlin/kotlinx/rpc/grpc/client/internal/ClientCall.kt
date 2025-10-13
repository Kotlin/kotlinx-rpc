/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * This class represents a client-side call to a server.
 * It provides the interface of the gRPC-Java ClientCall class; however, semantics are slightly different
 * on JVM and Native platforms.
 *
 * Callback execution:
 * - On JVM it is guaranteed that callbacks aren't executed concurrently.
 * - On Native, it is only guaranteed that `onClose` is called after all other callbacks finished.
 *
 * Sending message readiness:
 * - On JVM, it is possible to call [sendMessage] multiple times, without checking [isReady].
 *   Internally, it buffers the messages.
 * - On Native, you can only call [sendMessage] when [isReady] returns true. There is no buffering; therefore,
 *   only one message can be sent at a time.
 *
 * Request message number:
 * - On JVM, there is no limit on the number of messages you can [request].
 * - On Native, you can only call [request] with up to `16`.
 */
@InternalRpcApi
public expect abstract class ClientCall<Request, Response> {
    @InternalRpcApi
    public abstract class Listener<Message> {
        public open fun onHeaders(headers: GrpcMetadata)
        public open fun onMessage(message: Message)
        public open fun onClose(status: Status, trailers: GrpcMetadata)
        public open fun onReady()
    }

    public abstract fun start(responseListener: Listener<Response>, headers: GrpcMetadata)
    public abstract fun request(numMessages: Int)
    public abstract fun cancel(message: String?, cause: Throwable?)
    public abstract fun halfClose()
    public abstract fun sendMessage(message: Request)
    public open fun isReady(): Boolean
}

@InternalRpcApi
public expect fun <Message> clientCallListener(
    onHeaders: (headers: GrpcMetadata) -> Unit,
    onMessage: (message: Message) -> Unit,
    onClose: (status: Status, trailers: GrpcMetadata) -> Unit,
    onReady: () -> Unit,
): ClientCall.Listener<Message>
