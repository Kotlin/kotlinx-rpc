/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import io.grpc.testing.integration.TestService
import io.grpc.testing.integration.UnimplementedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.withService
import kxrpc.testing.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Owns the data and control clients for one isolated reference-server scenario.
 *
 * @param clientConfig Additional configuration applied to the data-plane client.
 */
internal class GrpcClientTestFixture(
    clientConfig: GrpcClientConfiguration.() -> Unit,
) {
    /** Unique metadata identifier used to correlate this test with its server-side trace. */
    internal val callId: String = newCallId()

    private val dataClient: GrpcClient = GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.PORT) {
        credentials = plaintext()
        clientConfig()
        intercept(CallIdInterceptor(callId))
    }
    private val controlClient: GrpcClient = GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.PORT) {
        credentials = plaintext()
    }
    private val controlService: GrpcClientControlService = controlClient.withService()

    /** Generated upstream interoperability service used for data-plane calls. */
    internal val testService: TestService = dataClient.withService()

    /** Generated upstream service which is intentionally absent from the reference server. */
    internal val unimplementedService: UnimplementedService = dataClient.withService()

    /** Generated client for deliberately invalid response-cardinality behavior. */
    internal val malformedResponseService: MalformedResponseService = dataClient.withService()

    private var scenarioSetupAttempted: Boolean = false
    private var scenarioConfigured: Boolean = false

    /** Registers this fixture's scenario before its data-plane call starts. */
    internal suspend fun configureScenario(
        barriers: List<Barrier> = emptyList(),
        configure: ConfigureScenarioRequest.Builder.() -> Unit = {},
    ) {
        scenarioSetupAttempted = true
        controlService.configureScenario(
            ConfigureScenarioRequest {
                callId = this@GrpcClientTestFixture.callId
                this.barriers = barriers
                configure()
            }
        )
        scenarioConfigured = true
    }

    /** Waits until the server records the requested one-based event occurrence. */
    internal suspend fun awaitServerEvent(
        event: EventType,
        occurrence: UInt = 1U,
    ): CallEvent {
        return controlService.awaitEvent(
            AwaitEventRequest {
                callId = this@GrpcClientTestFixture.callId
                this.event = event
                this.occurrence = occurrence
            }
        )
    }

    /** Releases one configured one-based reference-server barrier occurrence. */
    internal suspend fun releaseServerBarrier(
        barrier: BarrierType,
        occurrence: UInt = 1U,
    ) {
        controlService.releaseBarrier(
            ReleaseBarrierRequest {
                callId = this@GrpcClientTestFixture.callId
                this.barrier = barrier
                this.occurrence = occurrence
            }
        )
    }

    /** Grants the reference server demand for additional streaming request messages. */
    internal suspend fun grantInboundDemand(messageCount: UInt = 1U) {
        require(messageCount > 0U) { "inbound demand must be positive" }
        controlService.grantInboundDemand(
            GrantInboundDemandRequest {
                callId = this@GrpcClientTestFixture.callId
                this.messageCount = messageCount
            }
        )
    }

    /** Verifies an event is absent from the current trace, then releases its protecting barrier. */
    internal suspend fun assertServerEventAbsentUntil(
        event: EventType,
        barrier: BarrierType,
        occurrence: UInt = 1U,
    ) {
        val trace = serverTrace()
        assertEquals(
            null,
            trace.events.firstOrNull { it.type == event && it.occurrence == occurrence },
            "call_id='$callId', unexpectedly observed $event occurrence $occurrence:\n${trace.render()}",
        )
        releaseServerBarrier(barrier, occurrence)
    }

    /** Returns the server's ordered event trace for this fixture. */
    internal suspend fun serverTrace(): CallTrace {
        return controlService.getTrace(GetTraceRequest { callId = this@GrpcClientTestFixture.callId })
    }

    /** Returns server-side lifecycle and barrier state for teardown diagnostics. */
    internal suspend fun serverDiagnostics(): ScenarioDiagnostics {
        return controlService.getScenarioDiagnostics(
            GetScenarioDiagnosticsRequest { callId = this@GrpcClientTestFixture.callId }
        )
    }

    /** Awaits terminal server closure and verifies the complete ordered trace. */
    internal suspend fun assertServerTrace(expectedEvents: List<ExpectedServerEvent>) {
        awaitServerEvent(EventType.CALL_CLOSED)
        serverTrace().assertEvents(callId, expectedEvents)
    }

    /** Awaits server-observed client cancellation and verifies the complete ordered trace. */
    internal suspend fun assertServerCancelledTrace(expectedEvents: List<ExpectedServerEvent>) {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        serverTrace().assertEvents(callId, expectedEvents)
    }

    /** Awaits cancellation and accepts one of several explicitly documented event orderings. */
    internal suspend fun assertServerCancelledTraceOneOf(
        expectedAlternatives: List<List<ExpectedServerEvent>>,
    ) {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        serverTrace().assertEventsOneOf(callId, expectedAlternatives)
    }

    /** Requires one server-observed cancellation and no competing server close. */
    internal suspend fun assertServerObservedCancellation(): CallTrace {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        val trace = serverTrace()
        val failureMessage = "call_id='$callId', server trace:\n${trace.render()}"
        assertEquals(1, trace.events.count { it.type == EventType.CLIENT_CANCELLED }, failureMessage)
        assertEquals(0, trace.events.count { it.type == EventType.CALL_CLOSED }, failureMessage)
        assertEquals(EventType.CLIENT_CANCELLED, trace.events.lastOrNull()?.type, failureMessage)
        return trace
    }

    /** Accepts either server close or peer cancellation after an exact lifecycle prefix. */
    internal suspend fun assertServerTraceWithEitherTerminal(
        expectedBeforeTerminal: List<ExpectedServerEvent>,
    ) {
        serverTrace().assertEventsWithEitherTerminal(callId, expectedBeforeTerminal)
    }

    /** Compares response payloads with call and server-trace context on failure. */
    internal suspend fun assertResponsePayloads(
        expected: List<ByteArray>,
        actual: List<ByteArray>,
    ) {
        val trace = serverTrace()
        assertPayloadSequence(
            expected,
            actual,
            context = "call_id='$callId', server trace:\n${trace.render()}",
        )
    }

    /** Verifies the exact ordered request payload bytes observed by the reference server. */
    internal suspend fun assertServerRequestPayloads(expected: List<ByteArray>) {
        val trace = serverTrace()
        val actual = trace.events
            .filter { it.type == EventType.REQUEST_MESSAGE_RECEIVED }
            .map { it.requestPayload.toByteArray() }
        assertPayloadSequence(
            expected,
            actual,
            context = "call_id='$callId', server trace:\n${trace.render()}",
        )
    }

    /** Verifies the complete request-to-close lifecycle of one successful unary call. */
    internal suspend fun assertUnaryLifecycle() {
        assertServerTrace(successfulUnaryEvents())
    }

    /** Forcefully shuts down the data-plane client while leaving the control channel available. */
    internal fun shutdownDataClientNow() {
        dataClient.shutdownNow()
    }

    /**
     * Discards server state and closes both clients, preserving any cleanup failure.
     *
     * @param testFailure The test-body failure, or `null` after success.
     * @return The first cleanup failure with any additional failures suppressed.
     */
    internal suspend fun close(testFailure: Throwable?): Throwable? {
        val cleanup = GrpcClientTestCleanup()

        if (testFailure == null) {
            if (scenarioConfigured) {
                cleanup.run { assertScenarioCompleted() }
            }
            cleanup.run { dataClient.shutdown() }
        } else {
            cleanup.run { dataClient.shutdownNow() }
            if (scenarioConfigured) {
                cleanup.run {
                    println("grpcClientTest failed for call_id='$callId'; server trace:\n${serverTrace().render()}")
                }
                cleanup.run {
                    println("grpcClientTest server diagnostics for call_id='$callId': ${serverDiagnostics()}")
                }
            }
        }

        if (scenarioSetupAttempted) {
            cleanup.run {
                controlService.discardScenario(DiscardScenarioRequest { callId = this@GrpcClientTestFixture.callId })
                scenarioSetupAttempted = false
                scenarioConfigured = false
            }
        }

        cleanup.run { controlClient.shutdown() }
        cleanup.run { dataClient.awaitTermination(5.seconds) }
        cleanup.run { controlClient.awaitTermination(5.seconds) }

        return cleanup.failure()
    }

    private suspend fun assertScenarioCompleted() {
        var trace = serverTrace()
        if (trace.events.none { it.type.isTerminalServerEvent() }) {
            awaitServerEvent(EventType.CALL_CLOSED)
            trace = serverTrace()
        }
        val failureMessage = "call_id='$callId', server trace:\n${trace.render()}"
        val acceptedCallCount = trace.events.count { it.type == EventType.CALL_ACCEPTED }
        val closedCallCount = trace.events.count { it.type == EventType.CALL_CLOSED }
        val cancelledCallCount = trace.events.count { it.type == EventType.CLIENT_CANCELLED }
        assertEquals(acceptedCallCount, closedCallCount + cancelledCallCount, failureMessage)
        assertEquals(true, trace.events.lastOrNull()?.type?.isTerminalServerEvent(), failureMessage)
        serverDiagnostics().assertNoLeaks(callId, trace)
    }
}

private fun EventType.isTerminalServerEvent(): Boolean {
    return this == EventType.CALL_CLOSED || this == EventType.CLIENT_CANCELLED
}

private class CallIdInterceptor(
    private val callId: String,
) : GrpcClientInterceptor {
    override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response> {
        requestHeaders.append(GrpcClientTestServer.CALL_ID_METADATA_KEY, callId)
        return proceed(request)
    }
}

internal class GrpcClientTestCleanup(
    private val stepTimeout: Duration = 3.seconds,
) {
    private val failures = mutableListOf<Throwable>()

    internal suspend fun run(block: suspend () -> Unit) {
        try {
            withContext(Dispatchers.Default) {
                withTimeout(stepTimeout) { block() }
            }
        } catch (error: Throwable) {
            failures += error
        }
    }

    internal fun failure(): Throwable? = failures.combine()
}

private fun List<Throwable>.combine(): Throwable? {
    val first = firstOrNull() ?: return null
    drop(1).forEach(first::addSuppressed)
    return first
}

private fun newCallId(): String {
    val randomBytes = Random.nextBytes(16)
    return buildString(capacity = 49) {
        append("grpc-client-test-")
        randomBytes.forEach { byte ->
            val value = byte.toInt() and 0xff
            append(HEX_DIGITS[value ushr 4])
            append(HEX_DIGITS[value and 0x0f])
        }
    }
}

private const val HEX_DIGITS: String = "0123456789abcdef"
