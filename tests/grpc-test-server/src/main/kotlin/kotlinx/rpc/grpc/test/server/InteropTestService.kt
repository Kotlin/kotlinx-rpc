/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import grpc.testing.EmptyOuterClass.Empty
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.grpc.testing.integration.Messages.Payload
import io.grpc.testing.integration.Messages.ResponseParameters
import io.grpc.testing.integration.Messages.SimpleRequest
import io.grpc.testing.integration.Messages.SimpleResponse
import io.grpc.testing.integration.Messages.StreamingInputCallRequest
import io.grpc.testing.integration.Messages.StreamingInputCallResponse
import io.grpc.testing.integration.Messages.StreamingOutputCallRequest
import io.grpc.testing.integration.Messages.StreamingOutputCallResponse
import io.grpc.testing.integration.TestServiceGrpc
import java.util.ArrayDeque
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * The official gRPC interop `TestService`, following `TestServiceImpl` from
 * [grpc-java's v1.81.0 interop tests](https://github.com/grpc/grpc-java/tree/v1.81.0/interop-testing).
 *
 * Streaming responses are sent from [responseExecutor] so that they honour the requested intervals and,
 * for scenarios with response readiness, the client's readiness (see [ResponseReadinessGate]). Request-streaming
 * methods read requests only as the test grants demand, for scenarios with manual inbound demand (see
 * [enableManualInboundDemandIfConfigured]).
 * `UnimplementedCall` intentionally keeps the generated base implementation, which returns UNIMPLEMENTED.
 */
internal class InteropTestService(
    private val registry: CallScenarioRegistry,
    private val responseExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { command ->
        Thread(command, "grpc-client-test-response-dispatcher").apply { isDaemon = true }
    },
) : TestServiceGrpc.TestServiceImplBase(), AutoCloseable {
    override fun emptyCall(request: Empty, responseObserver: StreamObserver<Empty>) {
        responseObserver.onNext(Empty.getDefaultInstance())
        responseObserver.onCompleted()
    }

    override fun unaryCall(request: SimpleRequest, responseObserver: StreamObserver<SimpleResponse>) {
        val response = SimpleResponse.newBuilder()
        if (request.responseSize != 0) {
            val body = if (request.responseSize > 0) {
                ByteString.copyFrom(ByteArray(request.responseSize))
            } else {
                ByteString.EMPTY
            }
            response.setPayload(Payload.newBuilder().setBody(body))
        }

        if (request.hasResponseStatus()) {
            responseObserver.onError(
                Status.fromCodeValue(request.responseStatus.code)
                    .withDescription(request.responseStatus.message)
                    .asRuntimeException()
            )
            return
        }

        responseObserver.onNext(response.build())
        responseObserver.onCompleted()
    }

    override fun streamingOutputCall(
        request: StreamingOutputCallRequest,
        responseObserver: StreamObserver<StreamingOutputCallResponse>,
    ) {
        if (request.hasResponseStatus()) {
            responseObserver.onError(
                Status.fromCodeValue(request.responseStatus.code)
                    .withDescription(request.responseStatus.message)
                    .asRuntimeException()
            )
            return
        }

        ResponseDispatcher(responseObserver)
            .enqueue(request.toChunks())
            .completeInput()
    }

    override fun streamingInputCall(
        responseObserver: StreamObserver<StreamingInputCallResponse>,
    ): StreamObserver<StreamingInputCallRequest> {
        enableManualInboundDemandIfConfigured(registry, responseObserver)
        return object : StreamObserver<StreamingInputCallRequest> {
            private var totalPayloadSize: Int = 0

            override fun onNext(request: StreamingInputCallRequest) {
                totalPayloadSize += request.payload.body.size()
            }

            override fun onError(error: Throwable) {
                responseObserver.onError(error)
            }

            override fun onCompleted() {
                responseObserver.onNext(
                    StreamingInputCallResponse.newBuilder()
                        .setAggregatedPayloadSize(totalPayloadSize)
                        .build()
                )
                responseObserver.onCompleted()
            }
        }
    }

    override fun fullDuplexCall(
        responseObserver: StreamObserver<StreamingOutputCallResponse>,
    ): StreamObserver<StreamingOutputCallRequest> {
        enableManualInboundDemandIfConfigured(registry, responseObserver)
        val dispatcher = ResponseDispatcher(responseObserver)
        return object : StreamObserver<StreamingOutputCallRequest> {
            override fun onNext(request: StreamingOutputCallRequest) {
                if (request.hasResponseStatus()) {
                    dispatcher.cancel()
                    dispatcher.onError(
                        Status.fromCodeValue(request.responseStatus.code)
                            .withDescription(request.responseStatus.message)
                            .asRuntimeException()
                    )
                    return
                }
                dispatcher.enqueue(request.toChunks())
            }

            override fun onError(error: Throwable) {
                dispatcher.onError(error)
            }

            override fun onCompleted() {
                if (!dispatcher.isCancelled()) {
                    dispatcher.completeInput()
                }
            }
        }
    }

    override fun halfDuplexCall(
        responseObserver: StreamObserver<StreamingOutputCallResponse>,
    ): StreamObserver<StreamingOutputCallRequest> {
        enableManualInboundDemandIfConfigured(registry, responseObserver)
        val dispatcher = ResponseDispatcher(responseObserver)
        val chunks = ArrayDeque<ResponseChunk>()
        return object : StreamObserver<StreamingOutputCallRequest> {
            override fun onNext(request: StreamingOutputCallRequest) {
                chunks.addAll(request.toChunks())
            }

            override fun onError(error: Throwable) {
                dispatcher.onError(error)
            }

            override fun onCompleted() {
                dispatcher.enqueue(chunks).completeInput()
            }
        }
    }

    override fun close() {
        responseExecutor.shutdownNow()
    }

    private fun StreamingOutputCallRequest.toChunks(): Collection<ResponseChunk> {
        return responseParametersList.map { parameters -> parameters.toChunk() }
    }

    private fun ResponseParameters.toChunk(): ResponseChunk {
        return ResponseChunk(delayMicroseconds = intervalUs.toLong(), payloadSize = size)
    }

    /**
     * Sends the responses of one streaming call in order, each after its requested interval.
     *
     * The service handlers [enqueue] response chunks as requests arrive and call [completeInput] once no more will
     * follow. The chunks are sent one at a time from the shared [responseExecutor]: a chunk is scheduled only after
     * the previous one was sent, so each interval counts from the previous response and the order is preserved.
     *
     * With response readiness enabled in the scenario, [readinessGate] can hold a response back. Nothing is scheduled
     * while it does, and [resumeResponses] restarts delivery once the client is ready again.
     *
     * Ported from the `ResponseDispatcher` of the upstream grpc-java `TestServiceImpl` linked above. The
     * [readinessGate] integration was added here, as upstream sends responses regardless of readiness.
     *
     * The handlers call in on grpc-java's call executor, while delivery runs on [responseExecutor], so all state is
     * guarded by this dispatcher's monitor.
     *
     * @param responseObserver The observer the responses are sent to.
     */
    private inner class ResponseDispatcher(
        private val responseObserver: StreamObserver<StreamingOutputCallResponse>,
    ) {
        /** Chunks not sent yet, ending with [COMPLETION_CHUNK] once [completeInput] was called. */
        private val chunks = ArrayDeque<ResponseChunk>()

        /** `null` unless the scenario enables response readiness. */
        private val readinessGate = ResponseReadinessGate.createIfConfigured(
            registry = registry,
            responseObserver = responseObserver,
            resumeResponses = ::resumeResponses,
            cancelResponses = ::cancelFromTransport,
        )

        /** Whether a delivery is pending on [responseExecutor]. At most one is, which keeps responses in order. */
        private var scheduled: Boolean = false

        /** Set when the call was cancelled by the service or the client. Nothing is sent afterwards. */
        private var cancelled: Boolean = false

        /** The error that ended delivery, after which no more chunks are accepted. */
        private var failure: Throwable? = null

        /** Queues more responses and starts delivery if none is pending. */
        @Synchronized
        fun enqueue(moreChunks: Collection<ResponseChunk>): ResponseDispatcher {
            checkNotFailed()
            chunks.addAll(moreChunks)
            scheduleNextChunk()
            return this
        }

        /** Marks the end of the responses, so the call completes after every queued response was sent. */
        @Synchronized
        fun completeInput(): ResponseDispatcher {
            checkNotFailed()
            chunks.addLast(COMPLETION_CHUNK)
            scheduleNextChunk()
            return this
        }

        /** Drops the queued responses because the service ends the call itself, for example with an error status. */
        @Synchronized
        fun cancel() {
            check(!cancelled) { "Dispatcher already cancelled" }
            chunks.clear()
            cancelled = true
        }

        /**
         * Drops the queued responses after the client cancelled. Called by [readinessGate]'s cancel handler; unlike
         * [cancel], it tolerates an earlier cancellation.
         */
        @Synchronized
        private fun cancelFromTransport() {
            chunks.clear()
            cancelled = true
        }

        @Synchronized
        fun isCancelled(): Boolean = cancelled

        /** Fails the call right away, without waiting for queued responses. */
        @Synchronized
        fun onError(error: Throwable) {
            responseObserver.onError(error)
        }

        /** Runs on [responseExecutor]: sends the next chunk, then schedules the one after it. */
        private fun dispatch() {
            try {
                dispatchChunk()
            } finally {
                synchronized(this) {
                    scheduled = false
                    scheduleNextChunk()
                }
            }
        }

        /**
         * Sends the first queued chunk, or completes the call for [COMPLETION_CHUNK].
         *
         * A `CANCELLED` failure means the client is gone, so the remaining responses are dropped. Any other failure
         * is reported to the client.
         */
        @Synchronized
        private fun dispatchChunk() {
            if (cancelled) return
            try {
                val chunk = chunks.first()
                // Readiness is checked again, as it may have changed during the chunk's interval.
                if (isHeldBack(chunk)) return
                chunks.removeFirst()
                if (chunk === COMPLETION_CHUNK) {
                    responseObserver.onCompleted()
                } else {
                    responseObserver.onNext(chunk.toResponse())
                }
            } catch (error: Throwable) {
                failure = error
                if (Status.fromThrowable(error).code == Status.Code.CANCELLED) {
                    chunks.clear()
                } else {
                    responseObserver.onError(error)
                }
            }
        }

        /**
         * Schedules delivery of the first queued chunk after its interval. Does nothing while a delivery is pending,
         * the queue is empty, the service is closed, or [readinessGate] holds responses back.
         * Must be called holding this dispatcher's monitor.
         */
        private fun scheduleNextChunk() {
            if (scheduled || chunks.isEmpty() || responseExecutor.isShutdown) return
            if (isHeldBack(chunks.first())) return
            scheduled = true
            responseExecutor.schedule(
                ::dispatch,
                chunks.first().delayMicroseconds,
                TimeUnit.MICROSECONDS,
            )
        }

        /**
         * Whether [readinessGate] holds [chunk] back because the client is not ready. Completion is never held back,
         * as it adds no response message. Must be called holding this dispatcher's monitor.
         */
        private fun isHeldBack(chunk: ResponseChunk): Boolean {
            return chunk !== COMPLETION_CHUNK && readinessGate?.canDeliverResponse() == false
        }

        /** Called by [readinessGate] once the client is ready again after delivery was held back. */
        @Synchronized
        private fun resumeResponses() {
            scheduleNextChunk()
        }

        private fun checkNotFailed() {
            check(failure == null) { "Response stream already failed" }
        }
    }

    /**
     * One response of a streaming call, built from the request's `ResponseParameters`.
     *
     * @property delayMicroseconds Interval to wait before sending this response.
     * @property payloadSize Size of the zero-filled response payload in bytes.
     */
    private data class ResponseChunk(
        val delayMicroseconds: Long,
        val payloadSize: Int,
    ) {
        fun toResponse(): StreamingOutputCallResponse {
            val payload = if (payloadSize > 0) {
                ByteString.copyFrom(ByteArray(payloadSize))
            } else {
                ByteString.EMPTY
            }
            return StreamingOutputCallResponse.newBuilder()
                .setPayload(Payload.newBuilder().setBody(payload))
                .build()
        }
    }

    private companion object {
        /**
         * Queue marker for the end of a call's responses, sent as `onCompleted` instead of a message.
         * Compared by identity, since a requested response with zero interval and size is equal to it.
         */
        val COMPLETION_CHUNK: ResponseChunk = ResponseChunk(delayMicroseconds = 0, payloadSize = 0)
    }
}
