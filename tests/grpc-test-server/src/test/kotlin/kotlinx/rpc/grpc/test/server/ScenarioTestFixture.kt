/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.ServerInterceptors
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.AbstractStub
import io.grpc.stub.MetadataUtils
import io.grpc.stub.StreamObserver
import io.grpc.testing.integration.Messages.ResponseParameters
import io.grpc.testing.integration.Messages.StreamingOutputCallRequest
import java.net.InetSocketAddress
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallTrace
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GetScenarioDiagnosticsRequest
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GrantInboundDemandRequest
import kxrpc.testing.GrpcClientControlServiceGrpc
import kxrpc.testing.MetadataEntry
import kxrpc.testing.ReleaseBarrierRequest
import kxrpc.testing.ScenarioDiagnostics
import kotlin.test.assertEquals

/**
 * A test server on an ephemeral port with the scenario-aware interop, malformed-response, and control services,
 * wired like the real test server, plus a control client and a [dataChannel] for data-plane calls.
 */
internal class ScenarioTestFixture : AutoCloseable {
    private val registry = CallScenarioRegistry()
    private val interopService = InteropTestService(registry)
    private val scenarioInterceptor = ScenarioInterceptor(registry)
    private val server = NettyServerBuilder.forAddress(InetSocketAddress("127.0.0.1", 0))
        .addService(ServerInterceptors.intercept(interopService, scenarioInterceptor))
        .addService(ServerInterceptors.intercept(MalformedResponseTestService(registry), scenarioInterceptor))
        .addService(GrpcClientControlService(registry))
        .build()
        .start()
    private val controlChannel = channel()
    private val control = GrpcClientControlServiceGrpc.newBlockingStub(controlChannel)

    /** Channel for data-plane calls; select a scenario with [withCallId]. */
    val dataChannel: ManagedChannel = channel()

    fun configure(request: ConfigureScenarioRequest) {
        control.configureScenario(request)
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

    fun grantInboundDemand(callId: String, messageCount: Int) {
        control.grantInboundDemand(
            GrantInboundDemandRequest.newBuilder()
                .setCallId(callId)
                .setMessageCount(messageCount)
                .build()
        )
    }

    fun trace(callId: String): CallTrace = control.getTrace(GetTraceRequest.newBuilder().setCallId(callId).build())

    fun traceTypes(callId: String): List<EventType> = trace(callId).eventsList.map { it.type }

    fun diagnostics(callId: String): ScenarioDiagnostics {
        return control.getScenarioDiagnostics(GetScenarioDiagnosticsRequest.newBuilder().setCallId(callId).build())
    }

    /** Asserts that the scenario has no open calls, unreleased barriers, or blocked control requests. */
    fun assertNoLeaks(callId: String) {
        val diagnostics = diagnostics(callId)
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

    private fun channel(): ManagedChannel = NettyChannelBuilder.forAddress("127.0.0.1", server.port)
        .usePlaintext()
        .build()
}

internal fun withFixture(test: (ScenarioTestFixture) -> Unit) {
    ScenarioTestFixture().use(test)
}

/** Request metadata that selects the scenario [callId], followed by [requestMetadata]. */
internal fun callMetadata(callId: String, requestMetadata: Metadata = Metadata()): Metadata {
    return Metadata().apply {
        put(TEST_CALL_ID_METADATA_KEY, callId)
        merge(requestMetadata)
    }
}

/** Attaches [callMetadata] to every call made through this stub. */
internal fun <S : AbstractStub<S>> S.withCallId(callId: String, requestMetadata: Metadata = Metadata()): S {
    return withInterceptors(MetadataUtils.newAttachHeadersInterceptor(callMetadata(callId, requestMetadata)))
}

/** Records stream callbacks so a test can await them in order. */
internal class AwaitingObserver<T : Any> : StreamObserver<T> {
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

    /** Awaits events up to and including the terminal completion or error. */
    fun awaitUntilTerminal(): List<ObserverEvent<T>> = buildList {
        do {
            val event = awaitEvent()
            add(event)
        } while (event is ObserverEvent.Value)
    }

    private fun awaitEvent(): ObserverEvent<T> {
        return events.poll(5, TimeUnit.SECONDS) ?: throw AssertionError("Timed out waiting for stream event")
    }
}

internal sealed interface ObserverEvent<out T : Any> {
    data class Value<T : Any>(val value: T) : ObserverEvent<T>
    data class Error(val error: Throwable) : ObserverEvent<Nothing>
    data object Completed : ObserverEvent<Nothing>
}

/** The trace of a successful unary call without barriers or terminal behavior. */
internal val EXPECTED_UNARY_EVENTS: List<EventType> = listOf(
    EventType.CALL_ACCEPTED,
    EventType.REQUEST_MESSAGE_RECEIVED,
    EventType.CLIENT_HALF_CLOSED,
    EventType.INITIAL_HEADERS_SENT,
    EventType.RESPONSE_MESSAGE_SENT,
    EventType.CALL_CLOSED,
)

internal fun scenario(callId: String): ConfigureScenarioRequest.Builder =
    ConfigureScenarioRequest.newBuilder().setCallId(callId)

internal fun barrier(type: BarrierType, occurrence: Int = 1): Barrier =
    Barrier.newBuilder().setType(type).setOccurrence(occurrence).build()

internal fun metadataEntry(key: String, value: String): MetadataEntry = metadataEntry(key, value.encodeToByteArray())

internal fun metadataEntry(key: String, value: ByteArray): MetadataEntry =
    MetadataEntry.newBuilder().setKey(key).setValue(ByteString.copyFrom(value)).build()

internal fun streamingRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
    return StreamingOutputCallRequest.newBuilder()
        .addAllResponseParameters(responseSizes.map { size -> ResponseParameters.newBuilder().setSize(size).build() })
        .build()
}
