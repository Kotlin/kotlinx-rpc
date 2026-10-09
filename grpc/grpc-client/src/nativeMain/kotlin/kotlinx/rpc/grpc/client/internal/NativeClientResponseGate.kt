/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.statusCode

/**
 * Orders response callbacks whose C-core operations may complete concurrently.
 *
 * Delivers `onHeaders` at most once and always before the first message. This class is not
 * thread-safe: [NativeClientCall] serialises all calls through its callback mutex.
 */
internal class NativeClientResponseGate<Response>(
    private val onHeaders: (GrpcMetadata) -> Unit,
    private val onMessage: (Response) -> Unit,
) {
    private enum class HeadersState { PENDING, HELD_EMPTY, DELIVERED }

    private class PendingMessage<R>(val message: R, val requestNext: () -> Unit)

    private var headersState: HeadersState = HeadersState.PENDING

    // The receive pump does not request another message until this one is exposed, so at most
    // one message can wait for initial metadata.
    private var pendingMessage: PendingMessage<Response>? = null

    /**
     * Handles completion of the C-core initial-metadata operation.
     *
     * Empty headers are held back: C-core also reports them for a trailers-only response, so they
     * are only delivered once a message or an `OK` close proves a real headers phase.
     */
    internal fun initialMetadataReceived(headers: GrpcMetadata) {
        // Defensive: onHeaders is at-most-once.
        if (headersState == HeadersState.DELIVERED) return
        if (headers.keys().isEmpty() && pendingMessage == null) {
            headersState = HeadersState.HELD_EMPTY
            return
        }
        deliverHeaders(headers)
    }

    /** Handles a decoded response while preserving one-at-a-time inbound demand. */
    internal fun messageReceived(message: Response, requestNext: () -> Unit) {
        when (headersState) {
            HeadersState.PENDING -> {
                check(pendingMessage == null) {
                    internalError("A response message is already pending initial metadata")
                }
                pendingMessage = PendingMessage(message, requestNext)
                return
            }

            HeadersState.HELD_EMPTY -> deliverHeaders(GrpcMetadata())
            HeadersState.DELIVERED -> Unit
        }
        onMessage(message)
        requestNext()
    }

    /** Resolves delayed headers and messages immediately before the terminal callback. */
    internal fun flushBeforeClose(status: GrpcStatus) {
        val deliver = when (headersState) {
            HeadersState.DELIVERED -> false
            // Held empty headers followed by a non-OK close are the trailers-only artifact: drop them.
            HeadersState.HELD_EMPTY -> status.statusCode == GrpcStatusCode.OK
            HeadersState.PENDING -> pendingMessage != null
        }
        if (deliver) deliverHeaders(GrpcMetadata())
    }

    private fun deliverHeaders(headers: GrpcMetadata) {
        headersState = HeadersState.DELIVERED
        onHeaders(headers)
        pendingMessage?.let { pending ->
            pendingMessage = null
            onMessage(pending.message)
            // Submit the next RECV_MESSAGE only once this one is exposed to the listener.
            pending.requestNext()
        }
    }
}
