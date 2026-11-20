/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.server.internal.ServerCall

/**
 * Records adapter operations and checks their order without a network connection. Use from one thread.
 * The `before` callbacks run after contract checks, before an operation changes state or records its event.
 */
internal class FakeServerCall<Request, Response>(
    private val descriptor: GrpcMethodDescriptor<Request, Response>,
) : ServerCall<Request, Response>() {
    val events: MutableList<ServerCallEvent> = mutableListOf()
    var ready: Boolean = true
    var cancelled: Boolean = false

    var availableRequestCredits: Long = 0
        private set

    var beforeRequest: (Int) -> Unit = {}
    var beforeSendHeaders: (GrpcMetadata) -> Unit = {}
    var beforeSendMessage: (Response) -> Unit = {}
    var beforeClose: (GrpcStatus, GrpcMetadata) -> Unit = { _, _ -> }

    /** Runs inside [sendMessage], after the message event enters [events]. */
    var onSendMessage: (Response) -> Unit = {}

    private var headersSent = false
    private var closed = false

    override fun request(numMessages: Int) {
        violationIf(numMessages <= 0, "request count must be positive")
        beforeRequest(numMessages)
        availableRequestCredits += numMessages
        events += ServerCallEvent.Requested(numMessages)
    }

    fun consumeRequestCredit() {
        check(availableRequestCredits > 0) { "The adapter hasn't requested another message" }
        availableRequestCredits--
    }

    override fun sendHeaders(headers: GrpcMetadata) {
        violationIf(headersSent, "sendHeaders after sendHeaders")
        violationIf(closed, "sendHeaders after close")
        beforeSendHeaders(headers)
        headersSent = true
        events += ServerCallEvent.HeadersSent(headers.copy())
    }

    override fun sendMessage(message: Response) {
        violationIf(!headersSent, "sendMessage before sendHeaders")
        violationIf(closed, "sendMessage after close")
        beforeSendMessage(message)
        events += ServerCallEvent.MessageSent(message)
        onSendMessage(message)
    }

    override fun close(status: GrpcStatus, trailers: GrpcMetadata) {
        violationIf(closed, "close after close")
        beforeClose(status, trailers)
        closed = true
        events += ServerCallEvent.Closed(status, trailers.copy())
    }

    override fun isReady(): Boolean = ready

    override fun isCancelled(): Boolean = cancelled

    override fun getMethodDescriptor(): GrpcMethodDescriptor<Request, Response> = descriptor

    fun outbound(): List<String> =
        events.filter { it !is ServerCallEvent.Requested && it !is ServerCallEvent.ContractViolation }.map { it.label }

    fun requestedTotal(): Int = events.filterIsInstance<ServerCallEvent.Requested>().sumOf { it.count }

    fun violations(): List<String> = events.filterIsInstance<ServerCallEvent.ContractViolation>().map { it.message }

    private fun violationIf(condition: Boolean, message: String) {
        if (condition) {
            events += ServerCallEvent.ContractViolation(message)
            throw IllegalStateException(message)
        }
    }
}
