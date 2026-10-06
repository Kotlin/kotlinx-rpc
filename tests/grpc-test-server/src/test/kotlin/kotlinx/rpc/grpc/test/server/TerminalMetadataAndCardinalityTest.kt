/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import grpc.testing.EmptyOuterClass.Empty
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.MetadataUtils
import io.grpc.testing.integration.TestServiceGrpc
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus
import kxrpc.testing.MalformedResponse
import kxrpc.testing.MalformedResponseCardinality
import kxrpc.testing.MalformedResponseRequest
import kxrpc.testing.MalformedResponseServiceGrpc
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
        assertEquals(EXPECTED_UNARY_EVENTS, trace.eventsList.map { it.type })
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
            assertEquals(listOf(EventType.CALL_ACCEPTED, EventType.CALL_CLOSED), fixture.traceTypes(callId))
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
            fixture.traceTypes(callId),
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
            fixture.traceTypes(callId),
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

    private fun ScenarioTestFixture.testClient(
        callId: String,
        requestMetadata: Metadata = Metadata(),
        responseHeaders: AtomicReference<Metadata> = AtomicReference(),
        responseTrailers: AtomicReference<Metadata> = AtomicReference(),
    ): TestServiceGrpc.TestServiceBlockingStub {
        return TestServiceGrpc.newBlockingStub(dataChannel)
            .withCallId(callId, requestMetadata)
            .withInterceptors(MetadataUtils.newCaptureMetadataInterceptor(responseHeaders, responseTrailers))
    }

    private fun ScenarioTestFixture.malformedResponseClient(
        callId: String,
    ): MalformedResponseServiceGrpc.MalformedResponseServiceBlockingStub {
        return MalformedResponseServiceGrpc.newBlockingStub(dataChannel).withCallId(callId)
    }

    private fun ScenarioTestFixture.asyncMalformedResponseClient(
        callId: String,
    ): MalformedResponseServiceGrpc.MalformedResponseServiceStub {
        return MalformedResponseServiceGrpc.newStub(dataChannel).withCallId(callId)
    }

    private companion object {
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
    }
}
