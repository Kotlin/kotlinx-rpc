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

// The implemented calls follow grpc-java's v1.81.0 interop TestServiceImpl behavior:
// https://github.com/grpc/grpc-java/blob/v1.81.0/interop-testing/src/main/java/io/grpc/testing/integration/TestServiceImpl.java
// UnimplementedCall intentionally inherits the generated base implementation so it returns UNIMPLEMENTED.
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

        ResponseDispatcher(responseObserver, controlsInboundDemand = false)
            .enqueue(request.toChunks())
            .completeInput()
    }

    override fun streamingInputCall(
        responseObserver: StreamObserver<StreamingInputCallResponse>,
    ): StreamObserver<StreamingInputCallRequest> {
        FlowControlSupport.create(
            registry = registry,
            responseObserver = responseObserver,
            controlsInboundDemand = true,
        )
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
        val dispatcher = ResponseDispatcher(responseObserver, controlsInboundDemand = true)
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
        val dispatcher = ResponseDispatcher(responseObserver, controlsInboundDemand = true)
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

    private inner class ResponseDispatcher(
        private val responseObserver: StreamObserver<StreamingOutputCallResponse>,
        controlsInboundDemand: Boolean,
    ) {
        private val chunks = ArrayDeque<ResponseChunk>()
        private val flowControl = FlowControlSupport.create(
            registry = registry,
            responseObserver = responseObserver,
            controlsInboundDemand = controlsInboundDemand,
            resumeResponses = ::resumeResponses,
            cancelResponses = ::cancelFromTransport,
        )
        private var scheduled: Boolean = false
        private var cancelled: Boolean = false
        private var failure: Throwable? = null

        @Synchronized
        fun enqueue(moreChunks: Collection<ResponseChunk>): ResponseDispatcher {
            checkNotFailed()
            chunks.addAll(moreChunks)
            scheduleNextChunk()
            return this
        }

        @Synchronized
        fun completeInput(): ResponseDispatcher {
            checkNotFailed()
            chunks.addLast(COMPLETION_CHUNK)
            scheduleNextChunk()
            return this
        }

        @Synchronized
        fun cancel() {
            check(!cancelled) { "Dispatcher already cancelled" }
            chunks.clear()
            cancelled = true
        }

        @Synchronized
        private fun cancelFromTransport() {
            chunks.clear()
            cancelled = true
        }

        @Synchronized
        fun isCancelled(): Boolean = cancelled

        @Synchronized
        fun onError(error: Throwable) {
            responseObserver.onError(error)
        }

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

        @Synchronized
        private fun dispatchChunk() {
            if (cancelled) return
            try {
                val chunk = chunks.first()
                if (chunk !== COMPLETION_CHUNK && flowControl?.canDeliverResponse() == false) return
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

        private fun scheduleNextChunk() {
            if (scheduled || chunks.isEmpty() || responseExecutor.isShutdown) return
            if (chunks.first() !== COMPLETION_CHUNK && flowControl?.canDeliverResponse() == false) return
            scheduled = true
            responseExecutor.schedule(
                ::dispatch,
                chunks.first().delayMicroseconds,
                TimeUnit.MICROSECONDS,
            )
        }

        @Synchronized
        private fun resumeResponses() {
            scheduleNextChunk()
        }

        private fun checkNotFailed() {
            check(failure == null) { "Response stream already failed" }
        }
    }

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
