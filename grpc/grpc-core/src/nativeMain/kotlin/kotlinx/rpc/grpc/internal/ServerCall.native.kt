/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual fun interface ServerCallHandler<Request, Response> {
    public actual fun startCall(
        call: ServerCall<Request, Response>,
        headers: GrpcMetadata,
    ): ServerCall.Listener<Request>
}

@InternalRpcApi
public actual abstract class ServerCall<Request, Response> {
    public actual abstract fun request(numMessages: Int)
    public actual abstract fun sendHeaders(headers: GrpcMetadata)
    public actual abstract fun sendMessage(message: Response)
    public actual abstract fun close(status: Status, trailers: GrpcMetadata)

    public actual open fun isReady(): Boolean {
        // Default implementation returns true - subclasses can override if they need flow control
        return true
    }

    public actual abstract fun isCancelled(): Boolean
    public actual abstract fun getMethodDescriptor(): MethodDescriptor<Request, Response>

    @InternalRpcApi
    public actual abstract class Listener<Request> {
        public actual open fun onMessage(message: Request) {}
        public actual open fun onHalfClose() {}
        public actual open fun onCancel() {}
        public actual open fun onComplete() {}
        public actual open fun onReady() {}
    }
}

@InternalRpcApi
public actual fun <State, Message> serverCallListener(
    state: State,
    onMessage: (State, message: Message) -> Unit,
    onHalfClose: (State) -> Unit,
    onCancel: (State) -> Unit,
    onComplete: (State) -> Unit,
    onReady: (State) -> Unit,
): ServerCall.Listener<Message> {
    return object : ServerCall.Listener<Message>() {
        override fun onMessage(message: Message) {
            onMessage(state, message)
        }

        override fun onHalfClose() {
            onHalfClose(state)
        }

        override fun onCancel() {
            onCancel(state)
        }

        override fun onComplete() {
            onComplete(state)
        }

        override fun onReady() {
            onReady(state)
        }
    }
}
