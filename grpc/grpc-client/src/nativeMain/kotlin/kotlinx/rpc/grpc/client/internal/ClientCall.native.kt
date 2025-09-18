/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.client.internal.ClientCall
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.experimental.ExperimentalNativeApi

@InternalRpcApi
public actual abstract class ClientCall<Request, Response> {
    public actual abstract fun start(
        responseListener: Listener<Response>,
        headers: GrpcMetadata,
    )

    public actual abstract fun request(numMessages: Int)
    public actual abstract fun cancel(message: String?, cause: Throwable?)
    public actual abstract fun halfClose()
    public actual abstract fun sendMessage(message: Request)
    public actual open fun isReady(): Boolean {
        // Default implementation returns true - subclasses can override if they need flow control
        return true
    }

    @InternalRpcApi
    public actual abstract class Listener<Message> {
        public actual open fun onHeaders(headers: GrpcMetadata) {
        }

        public actual open fun onClose(status: Status, trailers: GrpcMetadata) {
        }

        public actual open fun onMessage(message: Message) {
        }

        public actual open fun onReady() {
        }
    }
}

@InternalRpcApi
public actual fun <Message> clientCallListener(
    onHeaders: (headers: GrpcMetadata) -> Unit,
    onMessage: (message: Message) -> Unit,
    onClose: (status: Status, trailers: GrpcMetadata) -> Unit,
    onReady: () -> Unit,
): ClientCall.Listener<Message> {
    return object : ClientCall.Listener<Message>() {
        override fun onHeaders(headers: GrpcMetadata) {
            onHeaders(headers)
        }

        override fun onMessage(message: Message) {
            onMessage(message)
        }

        override fun onClose(status: Status, trailers: GrpcMetadata) {
            onClose(status, trailers)
        }

        override fun onReady() {
            onReady()
        }
    }
}
