/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.test.EventLog

/** The final adapter status and a copy of its trailers. */
internal class ObservedClose(val status: GrpcStatus, val trailers: GrpcMetadata)

/** A call observed after its server job starts, with metadata copies for later assertions. */
internal class ObservedCall(val method: String, val requestHeaders: GrpcMetadata) {
    internal val close = CompletableDeferred<ObservedClose>()

    suspend fun awaitClose(): ObservedClose =
        withTimeoutOrNull(AWAIT_TIMEOUT) { close.await() }
            ?: throw AssertionError("The server didn't close the call to $method in $AWAIT_TIMEOUT")
}

/** Records calls and their final status through the public server interceptor API. */
internal class ServerCallObserver : GrpcServerInterceptor {
    private val callLog = EventLog<ObservedCall>()

    val calls: List<ObservedCall> get() = callLog.events

    override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response> {
        val call = ObservedCall(method.getFullMethodName(), requestHeaders.copy())
        callLog.add(call)
        onClose { status, trailers -> call.close.complete(ObservedClose(status, trailers.copy())) }
        return proceed(request)
    }

    /** Waits for the call at the zero-based [index] among calls to [method]. */
    suspend fun awaitCall(method: String, index: Int = 0): ObservedCall {
        require(index >= 0) { "Call index must be nonnegative: $index" }
        return callLog.await(AWAIT_TIMEOUT, count = index + 1) { it.method == method }
    }
}
