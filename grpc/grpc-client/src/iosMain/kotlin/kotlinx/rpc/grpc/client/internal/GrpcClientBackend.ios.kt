/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.cinterop.BetaInteropApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.asException
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.merge
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import kotlin.time.Duration
import kotlin.time.TimeSource

internal actual fun GrpcClientBackend(
    channel: ManagedChannel,
    callCredentials: GrpcCallCredentials,
): GrpcClientBackend {
    check(channel is SwiftManagedChannel)
    return SwiftGrpcClientBackend(channel, callCredentials)
}

internal class SwiftGrpcClientBackend(
    private val channel: SwiftManagedChannel,
    private val clientCallCredentials: GrpcCallCredentials,
) : GrpcClientBackend {
    override fun <Request, Response> execute(
        method: GrpcMethodDescriptor<Request, Response>,
        requests: Flow<Request>,
        headers: GrpcMetadata,
        callOptions: GrpcCallOptions,
    ): Flow<GrpcClientCallEvents<Response>> = flow {
        coroutineScope {
            // Add client level call credentials to call specific call credentials
            callOptions.callCredentials += clientCallCredentials

            var credentialHeaders: GrpcMetadata? = null
            var failure: GrpcStatus? = null
            callOptions.callCredentials.applyToMetadata(
                requestAuthority = channel.authority,
                requestMethodFullName = method.getFullMethodName(),
                isTransportSecure = channel.secure,
                recordFailure = { failure = it },
                applyCallCredentials = { credentialHeaders = it },
            )

            // If metadata application fails, we abort the RPC call early
            failure?.let { throw it.asException() }
            // If there was no failure, the credential headers must not be null
            check(credentialHeaders != null)

            // Copy headers to avoid leaking the credentials into the user-owned headers
            val callHeaders = headers.copy().apply {
                merge(credentialHeaders)
            }

            val encodedRequests = requests.map {
                encodeSwiftRequest(method.requestMarshaller, it)
            }
            withRequestSource(encodedRequests) { requestSource ->
                // A deadline only exists when a finite positive timeout is configured.
                val deadline = callOptions.timeout.takeIf { it.isFinite() && it.isPositive() }
                val callStarted = TimeSource.Monotonic.markNow()
                var call: SwiftGrpcCallAdapter<Response>? = null
                var closed = false
                try {
                    call = channel.startCall(
                        method = method,
                        headers = callHeaders,
                        callOptions = callOptions,
                        requestSource = requestSource,
                    )

                    while (!closed) {
                        // A raced local deadline can surface as UNAVAILABLE from grpc-swift;
                        // normalizeDeadlineRace converts that one case to DEADLINE_EXCEEDED.
                        val event = call.nextEvent().normalizeDeadlineRace(deadline, callStarted.elapsedNow())
                        closed = event is GrpcClientCallEvents.Closed
                        emit(event)
                    }
                } finally {
                    if (!closed) {
                        withContext(NonCancellable) {
                            call?.cancelAndWait("Kotlin response collection stopped before the call closed")
                        }
                    }
                }
            }
        }
    }
}

/** Prepares one request and publishes it only after its exact byte count has been written. */
internal fun <Request> encodeSwiftRequest(
    marshaller: GrpcMarshaller<Request>,
    request: Request,
): SwiftGrpcRequestBytes {
    val prepared = marshaller.prepare(request)
    val storage = SwiftRequestMessageWriter(prepared.size)
    prepared.writeTo(storage)
    // grpc-swift sends the whole fixed-size allocation, so a short write would turn the
    // unwritten tail into part of the request.
    check(storage.remaining == 0) {
        "The marshaller wrote ${storage.written} of ${storage.size} declared bytes."
    }
    return storage.requestBytes()
}

/**
 * Normalizes a grpc-swift race between an expired local deadline and the resulting HTTP/2 reset.
 *
 * Once the deadline expires, cancellation propagates through the call and the peer may send an
 * `RST_STREAM(CANCEL)` before grpc-swift publishes its canonical deadline status. In that ordering,
 * grpc-swift reports `UNAVAILABLE` even though the configured timeout caused the call to terminate.
 * Convert only that recognizable reset after the local timeout has elapsed so genuine
 * `UNAVAILABLE` failures and resets received before the deadline remain unchanged.
 */
internal fun <Response> GrpcClientCallEvents<Response>.normalizeDeadlineRace(
    timeout: Duration?,
    elapsed: Duration,
): GrpcClientCallEvents<Response> {
    if (this !is GrpcClientCallEvents.Closed ||
        timeout == null ||
        elapsed < timeout ||
        status.statusCode != GrpcStatusCode.UNAVAILABLE ||
        status.description?.contains(DEADLINE_RST_STREAM_DESCRIPTION) != true
    ) {
        return this
    }

    return GrpcClientCallEvents.Closed(
        status = GrpcStatus(GrpcStatusCode.DEADLINE_EXCEEDED, "Deadline exceeded", status.cause),
        trailers = trailers,
    )
}

internal const val DEADLINE_RST_STREAM_DESCRIPTION: String = "RST_STREAM frame (0x8: cancel)"
