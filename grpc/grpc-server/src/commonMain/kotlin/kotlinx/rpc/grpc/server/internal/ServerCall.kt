/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public expect fun interface ServerCallHandler<Request, Response> {
    public fun startCall(call: kotlinx.rpc.grpc.server.internal.ServerCall<Request, Response>, headers: GrpcMetadata): kotlinx.rpc.grpc.server.internal.ServerCall.Listener<Request>
}

@InternalRpcApi
public expect abstract class ServerCall<Request, Response> {
    @InternalRpcApi
    public abstract class Listener<Request> {
        public open fun onMessage(message: Request)
        public open fun onHalfClose()
        public open fun onCancel()
        public open fun onComplete()
        public open fun onReady()
    }

    public abstract fun request(numMessages: Int)
    public abstract fun sendHeaders(headers: GrpcMetadata)
    public abstract fun sendMessage(message: Response)
    public abstract fun close(status: Status, trailers: GrpcMetadata)

    public open fun isReady(): Boolean
    public abstract fun isCancelled(): Boolean

    public abstract fun getMethodDescriptor(): MethodDescriptor<Request, Response>
}

@InternalRpcApi
public expect fun <State, Message> serverCallListener(
    state: State,
    onMessage: (State, message: Message) -> Unit,
    onHalfClose: (State) -> Unit,
    onCancel: (State) -> Unit,
    onComplete: (State) -> Unit,
    onReady: (State) -> Unit,
): kotlinx.rpc.grpc.server.internal.ServerCall.Listener<Message>
