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
import io.grpc.testing.integration.Messages.EchoStatus
import io.grpc.testing.integration.Messages.Payload
import io.grpc.testing.integration.Messages.ResponseParameters
import io.grpc.testing.integration.Messages.SimpleRequest
import io.grpc.testing.integration.Messages.SimpleResponse
import io.grpc.testing.integration.Messages.StreamingInputCallRequest
import io.grpc.testing.integration.Messages.StreamingInputCallResponse
import io.grpc.testing.integration.Messages.StreamingOutputCallRequest
import io.grpc.testing.integration.Messages.StreamingOutputCallResponse
import io.grpc.testing.integration.TestServiceGrpc
import io.grpc.testing.integration.UnimplementedServiceGrpc
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GetScenarioDiagnosticsRequest
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GrpcClientControlServiceGrpc
import kxrpc.testing.ReleaseBarrierRequest
import kxrpc.testing.ScenarioDiagnostics
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class InteropTestServiceTest {
    @Test
    fun officialCallsDoNotRequireScenarioMetadata() = withFixture { fixture ->
        val client = fixture.testClient()

        assertEquals(Empty.getDefaultInstance(), client.emptyCall(Empty.getDefaultInstance()))
        assertEquals(
            8,
            client.unaryCall(SimpleRequest.newBuilder().setResponseSize(8).build()).payload.body.size(),
        )
    }

    @Test
    fun unaryCallReturnsRequestedStatus() = withFixture { fixture ->
        val expectedMessage = "requested failure"
        val error = assertFailsWith<StatusRuntimeException> {
            fixture.testClient().unaryCall(
                SimpleRequest.newBuilder()
                    .setResponseStatus(
                        EchoStatus.newBuilder()
                            .setCode(Status.Code.DATA_LOSS.value())
                            .setMessage(expectedMessage)
                            .build()
                    )
                    .build()
            )
        }

        assertEquals(Status.Code.DATA_LOSS, error.status.code)
        assertEquals(expectedMessage, error.status.description)
    }

    @Test
    fun emptyCallReturnsEmptyResponseAndRecordsLifecycle() = withFixture { fixture ->
        val callId = "empty-call"
        fixture.configure(callId)

        val response = fixture.testClient(callId).emptyCall(Empty.getDefaultInstance())

        assertEquals(Empty.getDefaultInstance(), response)
        fixture.awaitEvent(callId, EventType.CALL_CLOSED)
        assertEquals(EXPECTED_UNARY_EVENTS, fixture.traceTypes(callId))
        fixture.discard(callId)
    }

    @Test
    fun unaryCallReturnsRequestedPayloadAndHonorsLifecycleBarriers() = withFixture { fixture ->
        val callId = "unary-call"
        fixture.configure(
            callId,
            BarrierType.SEND_INITIAL_HEADERS,
            BarrierType.SEND_RESPONSE,
            BarrierType.CLOSE_CALL,
        )
        val executor = Executors.newSingleThreadExecutor()
        try {
            val response = executor.submit<SimpleResponse> {
                fixture.testClient(callId).unaryCall(
                    SimpleRequest.newBuilder()
                        .setResponseSize(32)
                        .build()
                )
            }

            fixture.awaitEvent(callId, EventType.CLIENT_HALF_CLOSED)
            assertFalse(response.isDone)

            fixture.release(callId, BarrierType.SEND_INITIAL_HEADERS)
            fixture.awaitEvent(callId, EventType.INITIAL_HEADERS_SENT)
            fixture.release(callId, BarrierType.SEND_RESPONSE)
            fixture.awaitEvent(callId, EventType.RESPONSE_MESSAGE_SENT)
            fixture.release(callId, BarrierType.CLOSE_CALL)

            val message = response.get(5, TimeUnit.SECONDS)
            assertEquals(32, message.payload.body.size())
            assertContentEquals(ByteArray(32), message.payload.body.toByteArray())
            fixture.awaitEvent(callId, EventType.CALL_CLOSED)
            assertEquals(EXPECTED_UNARY_EVENTS, fixture.traceTypes(callId))
            fixture.discard(callId)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun requestMessageDeliveryWaitsForItsConfiguredOccurrenceBarrier() = withFixture { fixture ->
        val callId = "request-message-barrier"
        fixture.configure(callId, listOf(BarrierType.DELIVER_REQUEST_MESSAGE to 2))
        val responses = AwaitingObserver<StreamingOutputCallResponse>()
        val requests = fixture.asyncTestClient(callId).fullDuplexCall(responses)

        requests.onNext(streamingRequest(3))
        assertEquals(3, responses.awaitNext().payload.body.size())
        requests.onNext(streamingRequest(5))
        fixture.awaitEvent(callId, EventType.REQUEST_MESSAGE_RECEIVED, occurrence = 2)
        assertFalse(responses.hasEvent())
        val blocked = fixture.diagnostics(callId)
        assertEquals(1, blocked.activeCallCount)
        assertEquals(1, blocked.outstandingBarriersCount)
        assertEquals(BarrierType.DELIVER_REQUEST_MESSAGE, blocked.outstandingBarriersList.single().type)
        assertEquals(2, blocked.outstandingBarriersList.single().occurrence)
        assertEquals(0, blocked.controlWaiterCount)
        fixture.release(callId, BarrierType.DELIVER_REQUEST_MESSAGE, occurrence = 2)
        assertEquals(5, responses.awaitNext().payload.body.size())

        requests.onCompleted()
        responses.awaitCompleted()
        fixture.awaitEvent(callId, EventType.CALL_CLOSED)
        fixture.assertNoLeaks(callId)
        fixture.discard(callId)
    }

    @Test
    fun clientHalfCloseDeliveryWaitsForItsConfiguredBarrier() = withFixture { fixture ->
        val callId = "half-close-barrier"
        fixture.configure(callId, listOf(BarrierType.DELIVER_CLIENT_HALF_CLOSE to 1))
        val response = AwaitingObserver<StreamingInputCallResponse>()
        val requests = fixture.asyncTestClient(callId).streamingInputCall(response)

        requests.onNext(streamingInputRequest(7))
        requests.onCompleted()
        fixture.awaitEvent(callId, EventType.CLIENT_HALF_CLOSED)
        assertFalse(response.hasEvent())
        val requestEvent = fixture.trace(callId).eventsList
            .single { it.type == EventType.REQUEST_MESSAGE_RECEIVED }
        assertEquals(ByteString.copyFrom(ByteArray(7)), requestEvent.requestPayload)
        fixture.release(callId, BarrierType.DELIVER_CLIENT_HALF_CLOSE)

        assertEquals(7, response.awaitNext().aggregatedPayloadSize)
        response.awaitCompleted()
        fixture.awaitEvent(callId, EventType.CALL_CLOSED)
        fixture.assertNoLeaks(callId)
        fixture.discard(callId)
    }

    @Test
    fun streamingOutputCallReturnsRequestedPayloadsInOrder() = withFixture { fixture ->
        val responses = fixture.testClient()
            .streamingOutputCall(streamingRequest(0, 7, 13))
            .asSequence()
            .toList()

        assertEquals(listOf(0, 7, 13), responses.map { it.payload.body.size() })
    }

    @Test
    fun streamingOutputCallSupportsAnEmptyResponseStream() = withFixture { fixture ->
        val responses = fixture.testClient().streamingOutputCall(streamingRequest())

        assertFalse(responses.hasNext())
    }

    @Test
    fun streamingOutputCallReturnsRequestedStatus() = withFixture { fixture ->
        val expectedMessage = "streaming output requested failure"
        val error = assertFailsWith<StatusRuntimeException> {
            fixture.testClient().streamingOutputCall(
                StreamingOutputCallRequest.newBuilder()
                    .setResponseStatus(
                        EchoStatus.newBuilder()
                            .setCode(Status.Code.DATA_LOSS.value())
                            .setMessage(expectedMessage)
                            .build()
                    )
                    .build()
            ).hasNext()
        }

        assertEquals(Status.Code.DATA_LOSS, error.status.code)
        assertEquals(expectedMessage, error.status.description)
    }

    @Test
    fun streamingOutputCallGatesAnIndividualResponseOccurrence() = withFixture { fixture ->
        val callId = "response-message-barrier"
        fixture.configure(callId, listOf(BarrierType.SEND_RESPONSE to 2))
        val responses = AwaitingObserver<StreamingOutputCallResponse>()

        fixture.asyncTestClient(callId).streamingOutputCall(streamingRequest(3, 5), responses)

        assertEquals(3, responses.awaitNext().payload.body.size())
        fixture.awaitEvent(callId, EventType.RESPONSE_MESSAGE_SENT, occurrence = 1)
        assertFalse(responses.hasEvent())
        fixture.release(callId, BarrierType.SEND_RESPONSE, occurrence = 2)
        assertEquals(5, responses.awaitNext().payload.body.size())
        responses.awaitCompleted()
        fixture.awaitEvent(callId, EventType.CALL_CLOSED)
        fixture.assertNoLeaks(callId)
        fixture.discard(callId)
    }

    @Test
    fun streamingInputCallReturnsAggregatedPayloadSize() = withFixture { fixture ->
        val response = AwaitingObserver<StreamingInputCallResponse>()
        val requests = fixture.asyncTestClient().streamingInputCall(response)

        requests.onNext(streamingInputRequest(3))
        requests.onNext(streamingInputRequest(5))
        requests.onNext(streamingInputRequest(8))
        requests.onCompleted()

        assertEquals(16, response.awaitNext().aggregatedPayloadSize)
        response.awaitCompleted()
    }

    @Test
    fun fullDuplexCallStreamsEachResponseBeforeClientHalfClose() = withFixture { fixture ->
        val responses = AwaitingObserver<StreamingOutputCallResponse>()
        val requests = fixture.asyncTestClient().fullDuplexCall(responses)

        requests.onNext(streamingRequest(3))
        assertEquals(3, responses.awaitNext().payload.body.size())
        requests.onNext(streamingRequest(5))
        assertEquals(5, responses.awaitNext().payload.body.size())

        requests.onCompleted()
        responses.awaitCompleted()
    }

    @Test
    fun fullDuplexCallReturnsRequestedStatus() = withFixture { fixture ->
        val expectedMessage = "stream requested failure"
        val responses = AwaitingObserver<StreamingOutputCallResponse>()
        val requests = fixture.asyncTestClient().fullDuplexCall(responses)

        requests.onNext(
            StreamingOutputCallRequest.newBuilder()
                .setResponseStatus(
                    EchoStatus.newBuilder()
                        .setCode(Status.Code.DATA_LOSS.value())
                        .setMessage(expectedMessage)
                )
                .build()
        )

        val error = responses.awaitError()
        assertEquals(Status.Code.DATA_LOSS, Status.fromThrowable(error).code)
        assertEquals(expectedMessage, Status.fromThrowable(error).description)
    }

    @Test
    fun halfDuplexCallReturnsBufferedResponsesAfterClientHalfClose() = withFixture { fixture ->
        val responses = AwaitingObserver<StreamingOutputCallResponse>()
        val requests = fixture.asyncTestClient().halfDuplexCall(responses)

        requests.onNext(streamingRequest(2, 3))
        requests.onNext(streamingRequest(5))
        requests.onCompleted()

        assertEquals(listOf(2, 3, 5), List(3) { responses.awaitNext().payload.body.size() })
        responses.awaitCompleted()
    }

    @Test
    fun generatedDefaultsReturnUnimplementedForMethodAndService() = withFixture { fixture ->
        val methodError = assertFailsWith<StatusRuntimeException> {
            fixture.testClient().unimplementedCall(Empty.getDefaultInstance())
        }
        val serviceError = assertFailsWith<StatusRuntimeException> {
            fixture.unimplementedClient().unimplementedCall(Empty.getDefaultInstance())
        }

        assertEquals(Status.Code.UNIMPLEMENTED, methodError.status.code)
        assertEquals(Status.Code.UNIMPLEMENTED, serviceError.status.code)
    }

    private fun withFixture(test: (Fixture) -> Unit) {
        Fixture().use(test)
    }

    private class Fixture : AutoCloseable {
        private val registry = CallScenarioRegistry()
        private val interopService = InteropTestService()
        private val server = NettyServerBuilder.forAddress(InetSocketAddress("127.0.0.1", 0))
            .addService(
                ServerInterceptors.intercept(
                    interopService,
                    InteropMetadataInterceptor(registry),
                )
            )
            .addService(GrpcClientControlService(registry))
            .build()
            .start()
        private val dataChannel = channel()
        private val controlChannel = channel()
        private val control = GrpcClientControlServiceGrpc.newBlockingStub(controlChannel)

        fun configure(callId: String, vararg barriers: BarrierType) {
            configure(callId, barriers.map { it to 1 })
        }

        fun configure(callId: String, barriers: List<Pair<BarrierType, Int>>) {
            control.configureScenario(
                ConfigureScenarioRequest.newBuilder()
                    .setCallId(callId)
                    .addAllBarriers(
                        barriers.map { (type, occurrence) ->
                            Barrier.newBuilder()
                                .setType(type)
                                .setOccurrence(occurrence)
                                .build()
                        }
                    )
                    .build()
            )
        }

        fun testClient(callId: String? = null): TestServiceGrpc.TestServiceBlockingStub {
            if (callId == null) return TestServiceGrpc.newBlockingStub(dataChannel)
            val metadata = Metadata().apply {
                put(Metadata.Key.of(TEST_CALL_ID_METADATA_KEY_NAME, Metadata.ASCII_STRING_MARSHALLER), callId)
            }
            return TestServiceGrpc.newBlockingStub(dataChannel)
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata))
        }

        fun asyncTestClient(callId: String? = null): TestServiceGrpc.TestServiceStub {
            if (callId == null) return TestServiceGrpc.newStub(dataChannel)
            val metadata = Metadata().apply {
                put(Metadata.Key.of(TEST_CALL_ID_METADATA_KEY_NAME, Metadata.ASCII_STRING_MARSHALLER), callId)
            }
            return TestServiceGrpc.newStub(dataChannel)
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata))
        }

        fun unimplementedClient(): UnimplementedServiceGrpc.UnimplementedServiceBlockingStub {
            return UnimplementedServiceGrpc.newBlockingStub(dataChannel)
        }

        fun awaitEvent(callId: String, type: EventType, occurrence: Int = 1) {
            control.awaitEvent(
                AwaitEventRequest.newBuilder()
                    .setCallId(callId)
                    .setEvent(type)
                    .setOccurrence(occurrence)
                    .build()
            )
        }

        fun release(callId: String, type: BarrierType, occurrence: Int = 1) {
            control.releaseBarrier(
                ReleaseBarrierRequest.newBuilder()
                    .setCallId(callId)
                    .setBarrier(type)
                    .setOccurrence(occurrence)
                    .build()
            )
        }

        fun traceTypes(callId: String): List<EventType> {
            return trace(callId)
                .eventsList
                .map { it.type }
        }

        fun trace(callId: String) = control.getTrace(
            GetTraceRequest.newBuilder().setCallId(callId).build()
        )

        fun assertNoLeaks(callId: String) {
            val diagnostics = diagnostics(callId)
            assertEquals(0, diagnostics.activeCallCount)
            assertEquals(emptyList(), diagnostics.outstandingBarriersList)
            assertEquals(0, diagnostics.controlWaiterCount)
        }

        fun diagnostics(callId: String): ScenarioDiagnostics {
            return control.getScenarioDiagnostics(
                GetScenarioDiagnosticsRequest.newBuilder().setCallId(callId).build()
            )
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

        fun awaitNext(): T = when (val event = awaitEvent()) {
            is ObserverEvent.Value -> event.value
            is ObserverEvent.Error -> throw AssertionError("Stream failed before its next value", event.error)
            ObserverEvent.Completed -> throw AssertionError("Stream completed before its next value")
        }

        fun awaitCompleted() {
            when (val event = awaitEvent()) {
                ObserverEvent.Completed -> Unit
                is ObserverEvent.Error -> throw AssertionError("Stream failed instead of completing", event.error)
                is ObserverEvent.Value -> throw AssertionError("Stream produced an unexpected value: ${event.value}")
            }
        }

        fun awaitError(): Throwable = when (val event = awaitEvent()) {
            is ObserverEvent.Error -> event.error
            ObserverEvent.Completed -> throw AssertionError("Stream completed instead of failing")
            is ObserverEvent.Value -> throw AssertionError("Stream produced an unexpected value: ${event.value}")
        }

        fun hasEvent(): Boolean = events.isNotEmpty()

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

        fun streamingRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
            return StreamingOutputCallRequest.newBuilder()
                .addAllResponseParameters(
                    responseSizes.map { size -> ResponseParameters.newBuilder().setSize(size).build() }
                )
                .build()
        }

        fun streamingInputRequest(payloadSize: Int): StreamingInputCallRequest {
            return StreamingInputCallRequest.newBuilder()
                .setPayload(Payload.newBuilder().setBody(ByteString.copyFrom(ByteArray(payloadSize))))
                .build()
        }
    }
}
