/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import grpc.testing.EmptyOuterClass.Empty
import io.grpc.Metadata
import io.grpc.ServerInterceptors
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.MetadataUtils
import io.grpc.testing.integration.Messages.EchoStatus
import io.grpc.testing.integration.Messages.SimpleRequest
import io.grpc.testing.integration.Messages.SimpleResponse
import io.grpc.testing.integration.TestServiceGrpc
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GrpcClientControlServiceGrpc
import kxrpc.testing.ReleaseBarrierRequest
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

    private fun withFixture(test: (Fixture) -> Unit) {
        Fixture().use(test)
    }

    private class Fixture : AutoCloseable {
        private val registry = CallScenarioRegistry()
        private val server = NettyServerBuilder.forAddress(InetSocketAddress("127.0.0.1", 0))
            .addService(
                ServerInterceptors.intercept(
                    InteropTestService(),
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
            control.configureScenario(
                ConfigureScenarioRequest.newBuilder()
                    .setCallId(callId)
                    .addAllBarriers(
                        barriers.map { type ->
                            Barrier.newBuilder()
                                .setType(type)
                                .setOccurrence(1)
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

        fun awaitEvent(callId: String, type: EventType) {
            control.awaitEvent(
                AwaitEventRequest.newBuilder()
                    .setCallId(callId)
                    .setEvent(type)
                    .setOccurrence(1)
                    .build()
            )
        }

        fun release(callId: String, type: BarrierType) {
            control.releaseBarrier(
                ReleaseBarrierRequest.newBuilder()
                    .setCallId(callId)
                    .setBarrier(type)
                    .setOccurrence(1)
                    .build()
            )
        }

        fun traceTypes(callId: String): List<EventType> {
            return control.getTrace(GetTraceRequest.newBuilder().setCallId(callId).build())
                .eventsList
                .map { it.type }
        }

        fun discard(callId: String) {
            control.discardScenario(DiscardScenarioRequest.newBuilder().setCallId(callId).build())
        }

        override fun close() {
            dataChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
            controlChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
            server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
        }

        private fun channel() = NettyChannelBuilder.forAddress("127.0.0.1", server.port)
            .usePlaintext()
            .build()
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
    }
}
