/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import io.grpc.ClientCall
import io.grpc.Metadata
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias ClientCall<Request, Response> = ClientCall<Request, Response>

@InternalRpcApi
public actual inline fun <Message> clientCallListener(
    crossinline onHeaders: (headers: GrpcMetadata) -> Unit,
    crossinline onMessage: (message: Message) -> Unit,
    crossinline onClose: (status: Status, trailers: GrpcMetadata) -> Unit,
    crossinline onReady: () -> Unit,
): ClientCall.Listener<Message> {
    return object : ClientCall.Listener<Message>() {
        override fun onHeaders(headers: Metadata) {
            onHeaders(headers)
        }

        override fun onMessage(message: Message) {
            onMessage(message)
        }

        override fun onClose(status: Status, trailers: Metadata) {
            onClose(status, trailers)
        }

        override fun onReady() {
            onReady()
        }
    }
}
