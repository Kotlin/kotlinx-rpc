/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcCall
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcCallEvent
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcClosedEvent
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcHeadersEvent
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcMessageEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Bridges the Kotlin side with the Swift gRPC call object.
 */
internal class SwiftGrpcCallAdapter<Response>(
    private val call: SwiftGrpcCall,
    private val method: GrpcMethodDescriptor<*, Response>,
) {
    /**
     * Pull the next event from the Swift gRPC call object.
     *
     * If the last event was [GrpcClientCallEvents.Closed], this method must not be called again.
     */
    suspend fun nextEvent(): GrpcClientCallEvents<Response> {
        val event = awaitSwiftGrpcCompletion(
            onCancellation = { cancel("Kotlin response collection was cancelled") },
            register = call::nextEventWithCompletion,
        ) ?: error("grpc-swift completed an event pull without an event or error")

        return when (event) {
            is SwiftGrpcHeadersEvent -> GrpcClientCallEvents.Headers(event.headers.toKotlin())
            is SwiftGrpcMessageEvent -> GrpcClientCallEvents.Message(event.decodeMessage())
            is SwiftGrpcClosedEvent -> GrpcClientCallEvents.Closed(
                status = event.status.toKotlin(),
                trailers = event.trailers.toKotlin(),
            )
            else -> error("grpc-swift returned an unknown call event: ${event::class}")
        }
    }

    fun cancel(message: String?) {
        call.cancelWithMessage(message)
    }

    suspend fun cancelAndWait(message: String?) {
        suspendCoroutine { continuation ->
            call.cancelAndWaitWithMessage(message) {
                continuation.resume(Unit)
            }
        }
    }

    private fun SwiftGrpcMessageEvent.decodeMessage(): Response {
        var result: Result<Response>? = null
        withUnsafeBytes { bytes, length ->
            // Catch it and rethrow it after returning into Kotlin.
            // The Kotlin-Swift-Kotlin boundary can't propagate Kotlin exceptions.
            result = runCatching {
                check(length == this.length) {
                    "grpc-swift message length changed from ${this.length} to $length during scoped access"
                }
                require(length in 0..Int.MAX_VALUE.toLong()) { "grpc-swift message size is out of range: $length" }
                SwiftResponseMessageReader(bytes, length.toInt()).use { reader ->
                    method.responseMarshaller.decode(reader)
                }
            }
        }
        return checkNotNull(result) { "grpc-swift did not provide scoped access to its message bytes" }.getOrThrow()
    }
}
