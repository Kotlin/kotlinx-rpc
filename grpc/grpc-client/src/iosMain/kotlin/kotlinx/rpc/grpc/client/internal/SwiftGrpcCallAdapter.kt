/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCall
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCallEvent
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcClosedEvent
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcHeadersEvent
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMessageEvent

/**
 * Bridges the Kotlin side with the Swift gRPC call object.
 *
 * The Swift gRPC call object is obtained from stating the call on the Swift gRPC client.
 * It provides a [nextEvent] method that pulls the next [SwiftGrpcCallEvent] by calling the
 * completion handler once an event is available.
 * This is handled
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

    private fun SwiftGrpcMessageEvent.decodeMessage(): Response {
        var result: Result<Response>? = null
        withUnsafeBytes { bytes, length ->
            result = runCatching {
                check(length == this.length) {
                    "grpc-swift message length changed from ${this.length} to $length during scoped access"
                }
                method.responseMarshaller.decode(copySwiftBytes(bytes, length))
            }
        }
        return checkNotNull(result) { "grpc-swift did not provide scoped access to its message bytes" }.getOrThrow()
    }
}
