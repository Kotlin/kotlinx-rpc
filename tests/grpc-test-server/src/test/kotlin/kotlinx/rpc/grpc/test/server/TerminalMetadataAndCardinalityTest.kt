/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import grpc.testing.EmptyOuterClass.Empty
import io.grpc.Metadata
import io.grpc.ServerInterceptors
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.MetadataUtils
import io.grpc.stub.StreamObserver
import io.grpc.testing.integration.Messages.ResponseParameters
import io.grpc.testing.integration.Messages.StreamingOutputCallRequest
import io.grpc.testing.integration.TestServiceGrpc
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallTrace
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GetScenarioDiagnosticsRequest
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GrpcClientControlServiceGrpc
import kxrpc.testing.GrpcStatus
import kxrpc.testing.MalformedResponse
import kxrpc.testing.MalformedResponseCardinality
import kxrpc.testing.MalformedResponseRequest
import kxrpc.testing.MalformedResponseServiceGrpc
import kxrpc.testing.MetadataEntry
import kxrpc.testing.ReleaseBarrierRequest
import kxrpc.testing.TerminalBehavior
import kxrpc.testing.TerminalStage
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TerminalMetadataAndCardinalityTest {
    @Test
    fun configuredMetadataAndTerminalStatusArePreservedAndTraced() = withFixture { fixture ->
        val callId = "metadata-terminal"
        val initialMetadata = listOf(
            metadataEntry("x-response-value", "first"),
            metadataEntry("x-response-value", ""),
            metadataEntry("x-response-bin", byteArrayOf(0, -1, 42)),
        )
        val trailingMetadata = listOf(
            metadataEntry("x-trailer-value", "done"),
            metadataEntry("x-trailer-value", ""),
        )
        fixture.configure(
            scenario(callId)
                .addAllInitialMetadata(initialMetadata)
                .addAllTrailingMetadata(trailingMetadata)
                .setTerminalBehavior(
                    terminalBehavior(
                        Status.Code.DATA_LOSS,
                        "configured terminal",
                        TerminalStage.AFTER_SERVICE_COMPLETION,
                    )
                )
                .build()
        )

        val requestMetadata = Metadata().apply {
            put(asciiKey("x-request-value"), "alpha")
            put(asciiKey("x-request-value"), "")
            put(binaryKey("x-request-bin"), byteArrayOf(1, 2, -1))
        }
        val responseHeaders = AtomicReference<Metadata>()
        val responseTrailers = AtomicReference<Metadata>()

        val error = assertFailsWith<StatusRuntimeException> {
            fixture.testClient(callId, requestMetadata, responseHeaders, responseTrailers)
                .emptyCall(Empty.getDefaultInstance())
        }

        assertEquals(Status.Code.DATA_LOSS, error.status.code)
        assertEquals("configured terminal", error.status.description)
        assertEquals(listOf("first", ""), assertNotNull(responseHeaders.get()).asciiValues("x-response-value"))
        assertContentEquals(
            byteArrayOf(0, -1, 42),
            assertNotNull(responseHeaders.get()).binaryValues("x-response-bin").single(),
        )
        assertEquals(listOf("done", ""), assertNotNull(responseTrailers.get()).asciiValues("x-trailer-value"))

        val trace = fixture.trace(callId)
        val accepted = trace.eventsList.single { it.type == EventType.CALL_ACCEPTED }
        assertEquals(
            listOf(
                metadataEntry("x-request-value", "alpha"),
                metadataEntry("x-request-value", ""),
                metadataEntry("x-request-bin", byteArrayOf(1, 2, -1)),
            ),
            accepted.metadataList.filter { it.key.startsWith("x-request") },
        )
        val headersSent = trace.eventsList.single { it.type == EventType.INITIAL_HEADERS_SENT }
        assertEquals(initialMetadata, headersSent.metadataList.filter { it.key.startsWith("x-response") })
        val closed = trace.eventsList.single { it.type == EventType.CALL_CLOSED }
        assertEquals(trailingMetadata, closed.metadataList.filter { it.key.startsWith("x-trailer") })
        assertEquals(Status.Code.DATA_LOSS.value(), closed.status.code)
        assertEquals("configured terminal", closed.status.description)
        assertEquals(EXPECTED_UNARY_EVENTS, trace.types())
        fixture.assertNoLeaks(callId)
        fixture.discard(callId)
    }

    @Test
    fun terminalBeforeInitialMetadataHonorsCloseBarrier() = withFixture { fixture ->
        val callId = "before-initial-metadata"
        fixture.configure(
            scenario(callId)
                .addBarriers(barrier(BarrierType.CLOSE_CALL))
                .addTrailingMetadata(metadataEntry("x-terminal-trailer", "before"))
                .setTerminalBehavior(
                    terminalBehavior(Status.Code.UNAVAILABLE, "before headers", TerminalStage.BEFORE_INITIAL_METADATA)
                )
                .build()
        )

        val executor = Executors.newSingleThreadExecutor()
        try {
            val result = executor.submit<StatusRuntimeException> {
                assertFailsWith {
                    fixture.testClient(callId).emptyCall(Empty.getDefaultInstance())
                }
            }

            fixture.awaitEvent(callId, EventType.CALL_ACCEPTED)
            assertFalse(result.isDone)
            fixture.release(callId, BarrierType.CLOSE_CALL)

            val error = result.get(5, TimeUnit.SECONDS)
            assertEquals(Status.Code.UNAVAILABLE, error.status.code)
            assertEquals("before", assertNotNull(error.trailers).asciiValues("x-terminal-trailer").single())
            fixture.awaitEvent(callId, EventType.CALL_CLOSED)
            assertEquals(listOf(EventType.CALL_ACCEPTED, EventType.CALL_CLOSED), fixture.trace(callId).types())
            fixture.assertNoLeaks(callId)
            fixture.discard(callId)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun terminalAfterInitialMetadataSendsHeadersWithoutAResponse() = withFixture { fixture ->
        val callId = "after-initial-metadata"
        fixture.configure(
            scenario(callId)
                .addInitialMetadata(metadataEntry("x-initial", "present"))
                .setTerminalBehavior(
                    terminalBehavior(Status.Code.INTERNAL, "after headers", TerminalStage.AFTER_INITIAL_METADATA)
                )
                .build()
        )
        val responseHeaders = AtomicReference<Metadata>()
        val responseTrailers = AtomicReference<Metadata>()

        val error = assertFailsWith<StatusRuntimeException> {
            fixture.testClient(callId, responseHeaders = responseHeaders, responseTrailers = responseTrailers)
                .emptyCall(Empty.getDefaultInstance())
        }

        assertEquals(Status.Code.INTERNAL, error.status.code)
        assertEquals("present", assertNotNull(responseHeaders.get()).asciiValues("x-initial").single())
        fixture.awaitEvent(callId, EventType.CALL_CLOSED)
        assertEquals(
            listOf(
                EventType.CALL_ACCEPTED,
                EventType.REQUEST_MESSAGE_RECEIVED,
                EventType.CLIENT_HALF_CLOSED,
                EventType.INITIAL_HEADERS_SENT,
                EventType.CALL_CLOSED,
            ),
            fixture.trace(callId).types(),
        )
        fixture.assertNoLeaks(callId)
        fixture.discard(callId)
    }

    @Test
    fun terminalAfterResponseCountPreservesEarlierMessages() = withFixture { fixture ->
        val callId = "after-response-count"
        fixture.configure(
            scenario(callId)
                .addTrailingMetadata(metadataEntry("x-failure-point", "two"))
                .setTerminalBehavior(
                    terminalBehavior(
                        Status.Code.RESOURCE_EXHAUSTED,
                        "after two responses",
                        TerminalStage.AFTER_RESPONSE_MESSAGES,
                        responseMessageCount = 2,
                    )
                )
                .build()
        )

        val responses = fixture.testClient(callId)
            .streamingOutputCall(streamingRequest(3, 5, 8))

        assertEquals(3, responses.next().payload.body.size())
        assertEquals(5, responses.next().payload.body.size())
        val error = assertFailsWith<StatusRuntimeException> { responses.hasNext() }
        assertEquals(Status.Code.RESOURCE_EXHAUSTED, error.status.code)
        assertEquals("two", assertNotNull(error.trailers).asciiValues("x-failure-point").single())
        fixture.awaitEvent(callId, EventType.CALL_CLOSED)
        assertEquals(
            listOf(
                EventType.CALL_ACCEPTED,
                EventType.REQUEST_MESSAGE_RECEIVED,
                EventType.CLIENT_HALF_CLOSED,
                EventType.INITIAL_HEADERS_SENT,
                EventType.RESPONSE_MESSAGE_SENT,
                EventType.RESPONSE_MESSAGE_SENT,
                EventType.CALL_CLOSED,
            ),
            fixture.trace(callId).types(),
        )
        fixture.assertNoLeaks(callId)
        fixture.discard(callId)
    }

    @Test
    fun unaryMalformedResponseHandlerSendsZeroOrTwoResponses() = withFixture { fixture ->
        listOf(
            MalformedResponseCardinality.OMIT_RESPONSE,
            MalformedResponseCardinality.DUPLICATE_RESPONSE,
        ).forEach { cardinality ->
            val callId = "unary-${cardinality.name.lowercase()}"
            fixture.configure(
                scenario(callId)
                    .setMalformedResponseCardinality(cardinality)
                    .build()
            )

            val error = assertFailsWith<StatusRuntimeException> {
                fixture.malformedResponseClient(callId).unaryCall(
                    MalformedResponseRequest.newBuilder()
                        .setPayload(ByteString.copyFromUtf8("unary"))
                        .build()
                )
            }

            assertFalse(error.status.isOk)
            fixture.awaitEvent(callId, EventType.CALL_CLOSED)
            val responseCount = fixture.trace(callId).eventsList
                .count { it.type == EventType.RESPONSE_MESSAGE_SENT }
            assertEquals(if (cardinality == MalformedResponseCardinality.OMIT_RESPONSE) 0 else 2, responseCount)
            fixture.assertNoLeaks(callId)
            fixture.discard(callId)
        }
    }

    @Test
    fun clientStreamingMalformedResponseHandlerSendsZeroOrTwoResponses() = withFixture { fixture ->
        listOf(
            MalformedResponseCardinality.OMIT_RESPONSE,
            MalformedResponseCardinality.DUPLICATE_RESPONSE,
        ).forEach { cardinality ->
            val callId = "client-streaming-${cardinality.name.lowercase()}"
            fixture.configure(
                scenario(callId)
                    .setMalformedResponseCardinality(cardinality)
                    .build()
            )
            val response = AwaitingObserver<MalformedResponse>()
            val requests = fixture.asyncMalformedResponseClient(callId).streamingInputCall(response)

            requests.onNext(
                MalformedResponseRequest.newBuilder()
                    .setPayload(ByteString.copyFromUtf8("first"))
                    .build()
            )
            requests.onNext(
                MalformedResponseRequest.newBuilder()
                    .setPayload(ByteString.copyFromUtf8("second"))
                    .build()
            )
            requests.onCompleted()

            val events = response.awaitUntilTerminal()
            if (cardinality == MalformedResponseCardinality.OMIT_RESPONSE) {
                assertEquals(0, events.count { it is ObserverEvent.Value })
                assertEquals(ObserverEvent.Completed, events.last())
            } else {
                assertEquals(1, events.count { it is ObserverEvent.Value })
                assertTrue(events.last() is ObserverEvent.Error)
            }
            fixture.awaitEvent(callId, EventType.CALL_CLOSED)
            val responseCount = fixture.trace(callId).eventsList
                .count { it.type == EventType.RESPONSE_MESSAGE_SENT }
            assertEquals(if (cardinality == MalformedResponseCardinality.OMIT_RESPONSE) 0 else 2, responseCount)
            fixture.assertNoLeaks(callId)
            fixture.discard(callId)
        }
    }

    private fun withFixture(test: (Fixture) -> Unit) {
        Fixture().use(test)
    }

    private class Fixture : AutoCloseable {
        private val registry = CallScenarioRegistry()
        private val interopService = InteropTestService(registry)
        private val scenarioInterceptor = InteropMetadataInterceptor(registry)
        private val server = NettyServerBuilder.forAddress(InetSocketAddress("127.0.0.1", 0))
            .addService(ServerInterceptors.intercept(interopService, scenarioInterceptor))
            .addService(ServerInterceptors.intercept(MalformedResponseTestService(registry), scenarioInterceptor))
            .addService(GrpcClientControlService(registry))
            .build()
            .start()
        private val dataChannel = channel()
        private val controlChannel = channel()
        private val control = GrpcClientControlServiceGrpc.newBlockingStub(controlChannel)

        fun configure(request: ConfigureScenarioRequest) {
            control.configureScenario(request)
        }

        fun testClient(
            callId: String,
            requestMetadata: Metadata = Metadata(),
            responseHeaders: AtomicReference<Metadata> = AtomicReference(),
            responseTrailers: AtomicReference<Metadata> = AtomicReference(),
        ): TestServiceGrpc.TestServiceBlockingStub {
            return TestServiceGrpc.newBlockingStub(dataChannel).withInterceptors(
                MetadataUtils.newAttachHeadersInterceptor(callMetadata(callId, requestMetadata)),
                MetadataUtils.newCaptureMetadataInterceptor(responseHeaders, responseTrailers),
            )
        }

        fun malformedResponseClient(
            callId: String,
        ): MalformedResponseServiceGrpc.MalformedResponseServiceBlockingStub {
            return MalformedResponseServiceGrpc.newBlockingStub(dataChannel)
                .withInterceptors(
                    MetadataUtils.newAttachHeadersInterceptor(callMetadata(callId)),
                )
        }

        fun asyncMalformedResponseClient(
            callId: String,
        ): MalformedResponseServiceGrpc.MalformedResponseServiceStub {
            return MalformedResponseServiceGrpc.newStub(dataChannel)
                .withInterceptors(
                    MetadataUtils.newAttachHeadersInterceptor(callMetadata(callId)),
                )
        }

        fun awaitEvent(callId: String, event: EventType) {
            control.awaitEvent(
                AwaitEventRequest.newBuilder()
                    .setCallId(callId)
                    .setEvent(event)
                    .setOccurrence(1)
                    .build()
            )
        }

        fun release(callId: String, barrier: BarrierType) {
            control.releaseBarrier(
                ReleaseBarrierRequest.newBuilder()
                    .setCallId(callId)
                    .setBarrier(barrier)
                    .setOccurrence(1)
                    .build()
            )
        }

        fun trace(callId: String): CallTrace {
            return control.getTrace(GetTraceRequest.newBuilder().setCallId(callId).build())
        }

        fun assertNoLeaks(callId: String) {
            val diagnostics = control.getScenarioDiagnostics(
                GetScenarioDiagnosticsRequest.newBuilder().setCallId(callId).build()
            )
            assertEquals(0, diagnostics.activeCallCount)
            assertEquals(emptyList(), diagnostics.outstandingBarriersList)
            assertEquals(0, diagnostics.controlWaiterCount)
        }

        fun discard(callId: String) {
            control.discardScenario(DiscardScenarioRequest.newBuilder().setCallId(callId).build())
        }

        override fun close() {
            dataChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
            controlChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
            server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
            interopService.close()
        }

        private fun callMetadata(callId: String, requestMetadata: Metadata = Metadata()): Metadata {
            return Metadata().apply {
                put(TEST_CALL_ID_METADATA_KEY, callId)
                merge(requestMetadata)
            }
        }

        private fun channel() = NettyChannelBuilder.forAddress("127.0.0.1", server.port)
            .usePlaintext()
            .build()
    }

    private class AwaitingObserver<T : Any> : StreamObserver<T> {
        private val events = LinkedBlockingQueue<ObserverEvent<T>>()

        override fun onNext(value: T) {
            events.add(ObserverEvent.Value(value))
        }

        override fun onError(error: Throwable) {
            events.add(ObserverEvent.Error(error))
        }

        override fun onCompleted() {
            events.add(ObserverEvent.Completed)
        }

        fun awaitUntilTerminal(): List<ObserverEvent<T>> = buildList {
            while (true) {
                when (val event = awaitEvent()) {
                    is ObserverEvent.Value -> {
                        add(event)
                    }
                    is ObserverEvent.Error -> {
                        add(event)
                        return@buildList
                    }
                    ObserverEvent.Completed -> {
                        add(event)
                        return@buildList
                    }
                }
            }
        }

        private fun awaitEvent(): ObserverEvent<T> {
            return events.poll(5, TimeUnit.SECONDS) ?: throw AssertionError("Timed out waiting for stream event")
        }
    }

    private sealed interface ObserverEvent<out T : Any> {
        data class Value<T : Any>(val value: T) : ObserverEvent<T>
        data class Error(val error: Throwable) : ObserverEvent<Nothing>
        data object Completed : ObserverEvent<Nothing>
    }

    private companion object {
        val EXPECTED_UNARY_EVENTS: List<EventType> = listOf(
            EventType.CALL_ACCEPTED,
            EventType.REQUEST_MESSAGE_RECEIVED,
            EventType.CLIENT_HALF_CLOSED,
            EventType.INITIAL_HEADERS_SENT,
            EventType.RESPONSE_MESSAGE_SENT,
            EventType.CALL_CLOSED,
        )

        fun scenario(callId: String): ConfigureScenarioRequest.Builder {
            return ConfigureScenarioRequest.newBuilder().setCallId(callId)
        }

        fun barrier(type: BarrierType): Barrier {
            return Barrier.newBuilder().setType(type).setOccurrence(1).build()
        }

        fun terminalBehavior(
            code: Status.Code,
            description: String,
            stage: TerminalStage,
            responseMessageCount: Int = 0,
        ): TerminalBehavior {
            return TerminalBehavior.newBuilder()
                .setStatus(
                    GrpcStatus.newBuilder()
                        .setCode(code.value())
                        .setDescription(description)
                )
                .setStage(stage)
                .setResponseMessageCount(responseMessageCount)
                .build()
        }

        fun metadataEntry(key: String, value: String): MetadataEntry {
            return metadataEntry(key, value.encodeToByteArray())
        }

        fun metadataEntry(key: String, value: ByteArray): MetadataEntry {
            return MetadataEntry.newBuilder()
                .setKey(key)
                .setValue(ByteString.copyFrom(value))
                .build()
        }

        fun asciiKey(name: String): Metadata.Key<String> {
            return Metadata.Key.of(name, Metadata.ASCII_STRING_MARSHALLER)
        }

        fun binaryKey(name: String): Metadata.Key<ByteArray> {
            return Metadata.Key.of(name, Metadata.BINARY_BYTE_MARSHALLER)
        }

        fun Metadata.asciiValues(name: String): List<String> {
            return getAll(asciiKey(name))?.toList().orEmpty()
        }

        fun Metadata.binaryValues(name: String): List<ByteArray> {
            return getAll(binaryKey(name))?.toList().orEmpty()
        }

        fun CallTrace.types(): List<EventType> = eventsList.map { it.type }

        fun streamingRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
            return StreamingOutputCallRequest.newBuilder()
                .addAllResponseParameters(
                    responseSizes.map { size -> ResponseParameters.newBuilder().setSize(size).build() }
                )
                .build()
        }
    }
}
