/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.grpc.*
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor

internal actual fun GrpcClientTransport(
    channel: ManagedChannel,
    callCredentials: GrpcCallCredentials,
): GrpcClientTransport {
    check(channel is SwiftManagedChannel)
    return SwiftGrpcClientTransport(channel, callCredentials)
}

internal class SwiftGrpcClientTransport(
    private val channel: SwiftManagedChannel,
    private val clientCallCredentials: GrpcCallCredentials,
) : GrpcClientTransport {
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

            // Turn flow into a request source that can be consumed by the Swift side using completion handlers.
            val requestSource = KotlinGrpcRequestSource(
                scope = this,
                requests = requests,
                encode = { request ->
                    KotlinGrpcRequestMessage(method.requestMarshaller.encode(request))
                },
            )

            var call: SwiftGrpcCallAdapter<Response>? = null
            var closed = false
            try {
                call = channel.startCall(
                    method = method,
                    headers = callHeaders,
                    timeout = callOptions.timeout,
                    compression = callOptions.compression,
                    requestSource = requestSource,
                )

                while (!closed) {
                    val event = call.nextEvent()
                    emit(event)
                    closed = event is GrpcClientCallEvents.Closed
                }
            } catch (cause: Throwable) {
                if (cause is SwiftGrpcInteropException) {
                    // Request failures cross Swift as NSError, losing the original Kotlin exception.
                    // Restore it here while leaving exceptions from Kotlin response handling intact.
                    // TODO: Swift must distinguish requestSource failures from purely Swift-side failures
                    //  so an unrelated Swift failure is not replaced by a stored request failure.
                    requestSource.originalFailure?.let { throw it }
                }
                throw cause
            } finally {
                requestSource.cancel()
                if (!closed) call?.cancel("Kotlin response collection stopped before the call closed")
            }
        }
    }
}
