/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import io.grpc.ServerCall
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias ServerCallHandler<Request, Response> = io.grpc.ServerCallHandler<Request, Response>

@InternalRpcApi
public actual typealias ServerCall<Request, Response> = ServerCall<Request, Response>

@InternalRpcApi
public actual inline fun <State, Message> serverCallListener(
    state: State,
    crossinline onMessage: (State, message: Message) -> Unit,
    crossinline onHalfClose: (State) -> Unit,
    crossinline onCancel: (State) -> Unit,
    crossinline onComplete: (State) -> Unit,
    crossinline onReady: (State) -> Unit,
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
