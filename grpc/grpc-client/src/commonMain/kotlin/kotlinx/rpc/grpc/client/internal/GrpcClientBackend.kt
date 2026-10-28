/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor

/**
 * An event of a gRPC client call.
 *
 * A backend emits the events of one call in this order:
 * ```
 * Headers? -> Message* -> Closed
 * ```
 * [GrpcClientCallEvents.Closed] comes exactly once and is the last event.
 * A [GrpcClientCallEvents.Message] never comes before [GrpcClientCallEvents.Headers].
 */
internal sealed class GrpcClientCallEvents<out Response> {
    data class Headers(val headers: GrpcMetadata) : GrpcClientCallEvents<Nothing>()
    data class Message<Response>(val response: Response) : GrpcClientCallEvents<Response>()
    data class Closed(
        val status: GrpcStatus,
        val trailers: GrpcMetadata,
    ) : GrpcClientCallEvents<Nothing>()
}

/**
 * The common platform API to the underlying gRPC runtime implementation.
 */
internal interface GrpcClientBackend {
    /**
     * Starts the call of [method] when the caller collects the returned flow.
     *
     * For unary and server-streaming methods, [requests] must emit exactly one request.
     * The caller must ensure this before calling [execute].
     */
    fun <Request, Response> execute(
        method: GrpcMethodDescriptor<Request, Response>,
        requests: Flow<Request>,
        headers: GrpcMetadata,
        callOptions: GrpcCallOptions,
    ): Flow<GrpcClientCallEvents<Response>>
}
