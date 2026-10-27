/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.internal.internalError

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
    private class PendingMessage<R>(val message: R, val requestNext: () -> Unit)

    private var headersDelivered = false

    // The receive pump does not request another message until this one is exposed, so at most
    // one message can wait for initial metadata.
    private var pendingMessage: PendingMessage<Response>? = null

    /** Handles completion of the C-core initial-metadata operation. */
    internal fun initialMetadataReceived(headers: GrpcMetadata) {
        // Defensive: onHeaders is at-most-once.
        if (headersDelivered) return
        deliverHeaders(headers)
    }

    /** Handles a decoded response while preserving one-at-a-time inbound demand. */
    internal fun messageReceived(message: Response, requestNext: () -> Unit) {
        if (!headersDelivered) {
            check(pendingMessage == null) {
                internalError("A response message is already pending initial metadata")
            }
            pendingMessage = PendingMessage(message, requestNext)
            return
        }
        onMessage(message)
        requestNext()
    }

    /**
     * Delivers empty headers and then the message that still waits for initial metadata, if there
     * is one. Call it immediately before the terminal callback.
     */
    internal fun deliverPendingMessage() {
        if (!headersDelivered && pendingMessage != null) deliverHeaders(GrpcMetadata())
    }

    private fun deliverHeaders(headers: GrpcMetadata) {
        headersDelivered = true
        onHeaders(headers)
        pendingMessage?.let { pending ->
            pendingMessage = null
            onMessage(pending.message)
            // Submit the next RECV_MESSAGE only once this one is exposed to the listener.
            pending.requestNext()
        }
    }
}
