/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor

/**
 * Represents events that can occur during a gRPC client call.
 *
 * There is a strict contract that the events are emitted in the following order:
 * ```
 * Headers? -> Message* -> Closed
 * ```
 * [GrpcClientCallEvents.Closed] is emitted exactly once and is terminal. A
 * [GrpcClientCallEvents.Message] is only emitted after [GrpcClientCallEvents.Headers].
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
 * Represents a gRPC client transport that can execute gRPC calls.
 *
 * It is the common boundary of different implementations (grpc-java and grpc-swift API).
 * Failure or cancellation of the event collection will cancel the active underlying RPC.
 */
internal interface GrpcClientTransport {
    fun <Request, Response> execute(
        method: GrpcMethodDescriptor<Request, Response>,
        requests: Flow<Request>,
        headers: GrpcMetadata,
        callOptions: GrpcCallOptions,
    ): Flow<GrpcClientCallEvents<Response>>
}

internal expect fun GrpcClientTransport(
    channel: ManagedChannel,
    callCredentials: GrpcCallCredentials,
): GrpcClientTransport
