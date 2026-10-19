/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import com.google.protobuf.Empty
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
 * The official gRPC interop `TestService`, ported from `TestServiceImpl` in the
 * [grpc-java v1.81.0 interop tests](https://github.com/grpc/grpc-java/tree/v1.81.0/interop-testing).
 *
 * [responseExecutor] sends the streaming responses at the requested intervals. For scenarios with response
 * readiness, the server also sends a response only while the client is ready (see [ResponseReadinessGate]). For
 * scenarios with manual inbound demand, request-streaming methods read a request only after the test grants demand
 * (see [enableManualInboundDemandIfConfigured]).
 *
 * `UnimplementedCall` uses the generated base implementation, which returns `UNIMPLEMENTED`.
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
     * The service handlers call [enqueue] with the response chunks of each request, and [completeInput] when no more
     * requests follow. All calls share [responseExecutor], which sends one chunk at a time. The dispatcher schedules a
     * chunk only after it sent the previous chunk, so each interval starts at the previous response and the order
     * doesn't change.
     *
     * If the scenario enables response readiness, [readinessGate] can hold back a response. The dispatcher schedules
     * nothing while it does, and [resumeResponses] restarts delivery when the client is ready again.
     *
     * Ported from the `ResponseDispatcher` of the grpc-java `TestServiceImpl` linked above. Upstream sends responses
     * regardless of readiness and has no [readinessGate].
     *
     * The handlers call the dispatcher on the call executor of grpc-java, and delivery runs on [responseExecutor], so
     * code reads and writes all state only while it holds the monitor of this dispatcher.
     */
    private inner class ResponseDispatcher(
        private val responseObserver: StreamObserver<StreamingOutputCallResponse>,
    ) {
        /** Chunks not sent yet. After [completeInput], the last chunk is [COMPLETION_CHUNK]. */
        private val chunks = ArrayDeque<ResponseChunk>()

        /** `null` unless the scenario enables response readiness. */
        private val readinessGate = ResponseReadinessGate.createIfConfigured(
            registry = registry,
            responseObserver = responseObserver,
            resumeResponses = ::resumeResponses,
            cancelResponses = ::cancelFromTransport,
        )

        /** Whether a delivery is pending on [responseExecutor]. At most one is pending, so responses stay in order. */
        private var scheduled: Boolean = false

        /** Set when the service or the client cancels the call. After that, the dispatcher sends nothing. */
        private var cancelled: Boolean = false

        /** The error that ended delivery. After it, [enqueue] and [completeInput] throw. */
        private var failure: Throwable? = null

        /** Queues more responses and starts delivery if none is pending. */
        @Synchronized
        fun enqueue(moreChunks: Collection<ResponseChunk>): ResponseDispatcher {
            checkNotFailed()
            chunks.addAll(moreChunks)
            scheduleNextChunk()
            return this
        }

        /** Marks the end of the responses, so the call completes after the dispatcher sends all queued responses. */
        @Synchronized
        fun completeInput(): ResponseDispatcher {
            checkNotFailed()
            chunks.addLast(COMPLETION_CHUNK)
            scheduleNextChunk()
            return this
        }

        /** Drops the queued responses when the service ends the call itself, for example with an error status. */
        @Synchronized
        fun cancel() {
            check(!cancelled) { "Dispatcher already cancelled" }
            chunks.clear()
            cancelled = true
        }

        /**
         * Drops the queued responses after the client cancels. The cancel handler of [readinessGate] calls it. Unlike
         * [cancel], it doesn't fail after an earlier cancellation.
         */
        @Synchronized
        private fun cancelFromTransport() {
            chunks.clear()
            cancelled = true
        }

        @Synchronized
        fun isCancelled(): Boolean = cancelled

        /** Fails the call and doesn't wait for the queued responses. */
        @Synchronized
        fun onError(error: Throwable) {
            responseObserver.onError(error)
        }

        /** Sends the next chunk, then schedules the chunk after it. Runs on [responseExecutor]. */
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
         * A `CANCELLED` failure means that the client is gone, so the dispatcher drops the remaining responses. The
         * dispatcher reports any other failure to the client.
         */
        @Synchronized
        private fun dispatchChunk() {
            if (cancelled) return
            try {
                val chunk = chunks.first()
                // Readiness can change during the interval of the chunk, so the dispatcher checks it again.
                if (isHeldBack(chunk)) return
                chunks.removeFirst()
                if (chunk === COMPLETION_CHUNK) {
                    responseObserver.onCompleted()
                } else {
                    responseObserver.onNext(chunk.toResponse())
                }
            } catch (error: Exception) {
                failure = error
                if (Status.fromThrowable(error).code == Status.Code.CANCELLED) {
                    chunks.clear()
                } else {
                    responseObserver.onError(error)
                }
            }
        }

        /**
         * Schedules delivery of the first queued chunk after its interval. The caller must hold the monitor of this
         * dispatcher.
         *
         * Does nothing in these cases:
         *
         * - A delivery is pending.
         * - The queue is empty.
         * - The service is closed.
         * - [readinessGate] holds back the first chunk.
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
         * Whether [readinessGate] holds back [chunk] because the client isn't ready. This function never holds back
         * [COMPLETION_CHUNK], because it adds no response message. The caller must hold the monitor of this
         * dispatcher.
         */
        private fun isHeldBack(chunk: ResponseChunk): Boolean {
            return chunk !== COMPLETION_CHUNK && readinessGate?.canDeliverResponse() == false
        }

        /** Restarts delivery. [readinessGate] calls it when the client is ready again after a pause. */
        @Synchronized
        private fun resumeResponses() {
            scheduleNextChunk()
        }

        private fun checkNotFailed() {
            check(failure == null) { "Response stream already failed" }
        }
    }

    /**
     * One response of a streaming call, built from the `ResponseParameters` of the request.
     *
     * @property delayMicroseconds The interval before the dispatcher sends this response.
     * @property payloadSize The size in bytes of the zero-filled response payload.
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
        val COMPLETION_CHUNK: ResponseChunk = ResponseChunk(delayMicroseconds = 0, payloadSize = 0)
    }
}
