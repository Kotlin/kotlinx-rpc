/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.internal.internalError

/**
 * Orders response callbacks whose C-core operations may complete concurrently.
 *
 * This class is not thread-safe. [NativeClientCall] serialises all calls through its callback
 * mutex.
 */
internal class NativeClientResponseGate<Response>(
    private val onHeaders: (GrpcMetadata) -> Unit,
    private val onMessage: (Response) -> Unit,
) {
    private enum class HeadersState { PENDING, HELD_EMPTY, DELIVERED }

    private class PendingMessage<R>(val message: R, val requestNext: () -> Unit)

    private var headersState: HeadersState = HeadersState.PENDING
    private var heldHeaders: GrpcMetadata? = null

    // The receive pump does not request another message until this one is exposed, so at most
    // one message can wait for initial metadata.
    private var pendingMessage: PendingMessage<Response>? = null

    /** Handles completion of the C-core initial-metadata operation. */
    internal fun initialMetadataReceived(headers: GrpcMetadata) {
        // Defensive: onHeaders is at-most-once.
        if (headersState == HeadersState.DELIVERED) return
        if (headers.keys().isEmpty() && pendingMessage == null) {
            heldHeaders = headers
            headersState = HeadersState.HELD_EMPTY
            return
        }
        deliverHeaders(headers)
    }

    /** Handles a decoded response while preserving one-at-a-time inbound demand. */
    internal fun messageReceived(message: Response, requestNext: () -> Unit) {
        when (headersState) {
            HeadersState.DELIVERED -> {
                onMessage(message)
                requestNext()
            }

            HeadersState.HELD_EMPTY -> {
                deliverHeaders(heldHeaders ?: GrpcMetadata())
                onMessage(message)
                requestNext()
            }

            HeadersState.PENDING -> {
                check(pendingMessage == null) {
                    internalError("A response message is already pending initial metadata")
                }
                pendingMessage = PendingMessage(message, requestNext)
            }
        }
    }

    /** Resolves delayed headers and messages immediately before the terminal callback. */
    internal fun flushBeforeClose(status: GrpcStatus) {
        when (headersState) {
            HeadersState.DELIVERED -> Unit

            HeadersState.HELD_EMPTY -> {
                if (status.statusCode == GrpcStatusCode.OK) {
                    deliverHeaders(heldHeaders ?: GrpcMetadata())
                } else {
                    heldHeaders = null
                }
            }

            HeadersState.PENDING -> {
                if (pendingMessage != null) deliverHeaders(GrpcMetadata())
            }
        }
    }

    private fun deliverHeaders(headers: GrpcMetadata) {
        headersState = HeadersState.DELIVERED
        heldHeaders = null
        onHeaders(headers)
        pendingMessage?.let { pending ->
            pendingMessage = null
            onMessage(pending.message)
            // Submit the next RECV_MESSAGE only once this one is exposed to the listener.
            pending.requestNext()
        }
    }
}
