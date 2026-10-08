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
import kxrpc.testing.AllowInboundReadsRequest
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallEvent
import kxrpc.testing.CallTrace
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GetScenarioDiagnosticsRequest
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GrpcClientControlService
import kxrpc.testing.InvalidResponseCountService
import kxrpc.testing.ReleaseBarrierRequest
import kxrpc.testing.ScenarioDiagnostics
import kxrpc.testing.StartDisposableEndpointRequest
import kxrpc.testing.StopDisposableEndpointRequest
import kxrpc.testing.invoke
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * The data-plane and control-plane clients of one isolated reference-server scenario.
 *
 * A fixture has two independent clients to [GrpcClientTestServer]:
 * - The **data-plane client** runs the calls under test through [testService], [unimplementedService], and
 *   [invalidResponseCountService]. It uses [clientConfig] and sends [callId] on every call. The server then applies
 *   the scenario of this fixture to the calls and records their lifecycle.
 * - The **control-plane client** calls `GrpcClientControlService`. It configures the scenario, synchronizes with
 *   the server ([awaitServerEvent], [releaseServerBarrier], [allowInboundReads]), and reads the server trace.
 *   It doesn't use [clientConfig] or send the call id. It stays usable after a test shuts down the data-plane
 *   client.
 *
 * Each fixture has a new random [callId], so concurrent tests on the shared server, such as the JVM and native
 * test tasks, never see the calls of each other.
 *
 * Tests usually get a fixture from [grpcClientTest], which also registers the scenario and calls [close].
 *
 * @param clientConfig Additional configuration of the data-plane client.
 */
internal class GrpcClientTestFixture(
    private val clientConfig: GrpcClientConfiguration.() -> Unit,
) {
    /**
     * Unique identifier that connects this test to its server-side trace.
     *
     * Every data-plane call sends it as [GrpcClientTestServer.CALL_ID_METADATA_KEY], and every control-plane call
     * has it as an argument.
     */
    internal val callId: String = newCallId()

    private val dataClient: GrpcClient = createDataClient(GrpcClientTestServer.port)
    private val controlClient: GrpcClient = GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.port) {
        credentials = plaintext()
    }
    private val controlService: GrpcClientControlService = controlClient.withService()

    /**
     * The official interop service `grpc.testing.TestService`.
     *
     * The reference server implements it like the grpc-java interop server does.
     */
    internal val testService: TestService = dataClient.withService()

    /**
     * An upstream service that the reference server doesn't implement.
     *
     * Every call fails with `UNIMPLEMENTED` before the server applies a scenario, so tests that use it pass
     * `configureScenario = false` to [grpcClientTest].
     */
    internal val unimplementedService: UnimplementedService = dataClient.withService()

    /**
     * A service that sends an invalid number of responses.
     *
     * The server answers its unary and client-streaming methods with zero or two responses, as the scenario
     * selects. Every call needs a configured scenario.
     */
    internal val invalidResponseCountService: InvalidResponseCountService = dataClient.withService()

    // Set before the client sends ConfigureScenario, so teardown also discards a scenario that the
    // server registered when the client saw the call fail.
    private var scenarioSetupAttempted: Boolean = false

    // Set after ConfigureScenario succeeds. Teardown then checks the scenario or prints its diagnostics.
    private var scenarioConfigured: Boolean = false
    private var disposableEndpointStarted: Boolean = false
    private var disposableDataClient: GrpcClient? = null

    /**
     * Registers the scenario of [callId] with [barriers] and the settings from [configure], such as terminal
     * behavior, response metadata, or flow control.
     *
     * Call it before the first data-plane call, and at most once for each fixture, because the server accepts one
     * registration for each call id. [grpcClientTest] calls it unless `configureScenario = false`.
     */
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

    /**
     * Waits until the server records the one-based [occurrence] of [event].
     *
     * Returns immediately if the server already recorded it. For example,
     * `awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT, 2U)` returns after the server sends its second response.
     * After 10 seconds, the server fails the wait with `DEADLINE_EXCEEDED`.
     *
     * @return The recorded event with its metadata and request payload.
     */
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

    /**
     * Releases the one-based [occurrence] of a configured reference-server [barrier].
     *
     * If the call waits at the barrier, the server continues it. Otherwise, the call later passes the barrier
     * without a stop. The server accepts one release for each occurrence. Release every configured barrier before
     * the test ends, or teardown reports it as leaked.
     */
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

    /**
     * Lets the reference server read at most [messageCount] more streaming request messages.
     *
     * Only for scenarios with manual inbound demand. In such a scenario, the server reads no request message until
     * the test allows reads. Requests that the server doesn't read stay in the transport, so a test can observe
     * the client under backpressure.
     */
    internal suspend fun allowInboundReads(messageCount: UInt = 1U) {
        require(messageCount > 0U) { "message count must be positive" }
        controlService.allowInboundReads(
            AllowInboundReadsRequest {
                callId = this@GrpcClientTestFixture.callId
                this.messageCount = messageCount
            }
        )
    }

    /**
     * Starts a service on an isolated listener that the test can stop, and returns the service.
     *
     * The server starts a listener for this scenario on a free port. The fixture creates a second data-plane
     * client for it, with the same configuration as the main one. [stopDisposableEndpoint] resets every TCP
     * connection of the listener, which simulates a lost connection and doesn't affect other tests. Call it at
     * most once for each fixture. [close] stops the listener and shuts down its client.
     */
    internal suspend fun startDisposableTestService(): TestService {
        check(disposableDataClient == null) { "disposable data client is already started" }
        val endpoint = controlService.startDisposableEndpoint(
            StartDisposableEndpointRequest { callId = this@GrpcClientTestFixture.callId }
        )
        disposableEndpointStarted = true
        val client = createDataClient(endpoint.port.toInt())
        disposableDataClient = client
        return client.withService()
    }

    /**
     * Stops the listener of [startDisposableTestService] and resets all its TCP connections.
     *
     * Calls on the listener fail like calls on a lost connection.
     */
    internal suspend fun stopDisposableEndpoint() {
        check(disposableEndpointStarted) { "disposable endpoint is not started" }
        controlService.stopDisposableEndpoint(
            StopDisposableEndpointRequest { callId = this@GrpcClientTestFixture.callId }
        )
        disposableEndpointStarted = false
    }

    /**
     * Checks that the current trace has no [occurrence] of [event], then releases this occurrence of [barrier].
     *
     * [barrier] must hold back [event], for example a [BarrierType.SEND_RESPONSE] barrier for an
     * [EventType.RESPONSE_MESSAGE_SENT]. The server can't pass the barrier before the release, so the check
     * can't race with the event.
     */
    internal suspend fun assertServerEventAbsentUntil(
        event: EventType,
        barrier: BarrierType,
        occurrence: UInt = 1U,
    ) {
        val trace = serverTrace()
        assertNull(
            trace.events.firstOrNull { it.type == event && it.occurrence == occurrence },
            "call_id='$callId', unexpected $event occurrence $occurrence:\n${trace.render()}",
        )
        releaseServerBarrier(barrier, occurrence)
    }

    /**
     * Returns the ordered server trace of this fixture.
     *
     * The trace is a snapshot, so a call that still runs can add events later.
     */
    internal suspend fun serverTrace(): CallTrace {
        return controlService.getTrace(GetTraceRequest { callId = this@GrpcClientTestFixture.callId })
    }

    /** Returns the server-side lifecycle and barrier state of the scenario, for teardown diagnostics. */
    internal suspend fun serverDiagnostics(): ScenarioDiagnostics {
        return controlService.getScenarioDiagnostics(
            GetScenarioDiagnosticsRequest { callId = this@GrpcClientTestFixture.callId }
        )
    }

    /**
     * Waits for [EventType.CALL_CLOSED], then compares the complete trace with [expectedEvents] (see
     * [assertEvents]).
     *
     * For a call that the client cancelled, use [assertServerCancelledTrace].
     */
    internal suspend fun assertServerTrace(expectedEvents: List<ExpectedServerEvent>) {
        awaitServerEvent(EventType.CALL_CLOSED)
        serverTrace().assertEvents(callId, expectedEvents)
    }

    /**
     * Waits for [EventType.CLIENT_CANCELLED], then compares the complete trace with [expectedEvents] (see
     * [assertEvents]).
     */
    internal suspend fun assertServerCancelledTrace(expectedEvents: List<ExpectedServerEvent>) {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        serverTrace().assertEvents(callId, expectedEvents)
    }

    /**
     * Waits for [EventType.CLIENT_CANCELLED], then checks that the trace matches one of [expectedAlternatives]
     * (see [assertEventsOneOf]).
     */
    internal suspend fun assertServerCancelledTraceOneOf(
        expectedAlternatives: List<List<ExpectedServerEvent>>,
    ) {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        serverTrace().assertEventsOneOf(callId, expectedAlternatives)
    }

    /**
     * Waits for [EventType.CLIENT_CANCELLED] and checks that the trace has it exactly once, as the last event, and
     * has no [EventType.CALL_CLOSED].
     *
     * Unlike [assertServerCancelledTrace], it doesn't check the events before the cancellation.
     *
     * @return The trace, for further checks.
     */
    internal suspend fun assertServerObservedCancellation(): CallTrace {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        val trace = serverTrace()
        val failureMessage = "call_id='$callId', server trace:\n${trace.render()}"
        assertEquals(1, trace.events.count { it.type == EventType.CLIENT_CANCELLED }, failureMessage)
        assertEquals(0, trace.events.count { it.type == EventType.CALL_CLOSED }, failureMessage)
        assertEquals(EventType.CLIENT_CANCELLED, trace.events.lastOrNull()?.type, failureMessage)
        return trace
    }

    /**
     * Checks that the trace is [expectedBeforeTerminal] followed by a server close or a client cancellation (see
     * [assertEventsWithEitherTerminal]).
     *
     * It doesn't wait for the terminal event, so call it only after the server ends the call.
     */
    internal suspend fun assertServerTraceWithEitherTerminal(
        expectedBeforeTerminal: List<ExpectedServerEvent>,
    ) {
        serverTrace().assertEventsWithEitherTerminal(callId, expectedBeforeTerminal)
    }

    /** Runs [assertPayloadSequence] with [callId] and the current server trace in the failure message. */
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

    /**
     * Checks the exact bytes and order of the request payloads that the reference server received.
     *
     * The server records the payload of each interop request on its [EventType.REQUEST_MESSAGE_RECEIVED] event,
     * so the check doesn't depend on what the client reports. It doesn't wait for more requests, so call it after
     * the server receives all of them.
     */
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

    /** Runs [assertServerTrace] with [successfulUnaryEvents]. */
    internal suspend fun assertUnaryLifecycle() {
        assertServerTrace(successfulUnaryEvents())
    }

    /** Shuts down the data-plane client and cancels its running calls. */
    internal fun shutdownDataClientNow() {
        dataClient.shutdownNow()
    }

    /**
     * Starts a graceful shutdown of the data-plane client.
     *
     * Running calls can complete, and the client rejects new calls.
     */
    internal fun shutdownDataClient() {
        dataClient.shutdown()
    }

    /** Waits at most [duration] for the data-plane client to terminate. */
    internal suspend fun awaitDataClientTermination(duration: Duration = 5.seconds) {
        dataClient.awaitTermination(duration)
    }

    /**
     * Discards the server state, closes all clients, and returns the cleanup failure.
     *
     * Every step runs through [GrpcClientTestCleanup], so a failing or hanging step doesn't skip the others. The
     * first steps depend on [testFailure]:
     * - After a successful test body, teardown checks a configured scenario. Every data-plane call must have
     *   ended, and the server must hold no open calls, barriers, or waiters (see [assertNoLeaks]). Then teardown
     *   shuts down the data-plane clients gracefully.
     * - After a failed test body, teardown first shuts down the data-plane clients forcefully, because calls can
     *   still wait at barriers. Then it prints the server trace and diagnostics of a configured scenario, and
     *   makes no assertions.
     *
     * Then teardown discards the scenario on the server if the fixture sent `ConfigureScenario`. This also stops
     * a disposable endpoint and fails the control calls that still wait on it. At the end, teardown waits for all
     * clients to terminate.
     *
     * @param testFailure The test-body failure, or `null` after success.
     * @return The first cleanup failure with the other failures suppressed, or `null`.
     */
    internal suspend fun close(testFailure: Throwable?): Throwable? {
        val cleanup = GrpcClientTestCleanup()

        if (testFailure == null) {
            if (disposableEndpointStarted) {
                cleanup.run { stopDisposableEndpoint() }
            }
            if (scenarioConfigured) {
                cleanup.run { assertScenarioCompleted() }
            }
            cleanup.run { dataClient.shutdown() }
            cleanup.run { disposableDataClient?.shutdown() }
        } else {
            cleanup.run { dataClient.shutdownNow() }
            cleanup.run { disposableDataClient?.shutdownNow() }
            if (disposableEndpointStarted) {
                cleanup.run { stopDisposableEndpoint() }
            }
            if (scenarioConfigured) {
                cleanup.run {
                    println("grpcClientTest failed for call_id='$callId'. Server trace:\n${serverTrace().render()}")
                }
                cleanup.run {
                    println("grpcClientTest server diagnostics for call_id='$callId': ${serverDiagnostics()}")
                }
            }
        }

        if (scenarioSetupAttempted) {
            cleanup.run {
                controlService.discardScenario(DiscardScenarioRequest { callId = this@GrpcClientTestFixture.callId })
            }
        }

        cleanup.run { controlClient.shutdown() }
        cleanup.run { dataClient.awaitTermination(5.seconds) }
        cleanup.run { disposableDataClient?.awaitTermination(5.seconds) }
        cleanup.run { controlClient.awaitTermination(5.seconds) }

        return cleanup.failure()
    }

    /**
     * Checks that the scenario ended cleanly. If no call ended yet, it first waits for [EventType.CALL_CLOSED].
     *
     * The checks:
     * - Every accepted data-plane call ended with a server close or a client cancellation.
     * - The trace ends with a server close or a client cancellation.
     * - The server holds no state for the scenario.
     */
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

    /**
     * Creates a plaintext data-plane client for [port] with [clientConfig].
     *
     * [CallIdInterceptor] comes after the interceptors of [clientConfig], so it runs last before the transport.
     */
    private fun createDataClient(port: Int): GrpcClient {
        return GrpcClient(GrpcClientTestServer.HOST, port) {
            credentials = plaintext()
            clientConfig()
            intercept(CallIdInterceptor(callId))
        }
    }
}

/** Whether the event ends a data-plane call on the server: a server close or a client cancellation. */
private fun EventType.isTerminalServerEvent(): Boolean {
    return this == EventType.CALL_CLOSED || this == EventType.CLIENT_CANCELLED
}

/** Adds [callId] to the request metadata of every data-plane call, so the server applies the scenario of [callId]. */
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

/**
 * Runs teardown steps independently of each other. If a step fails or hangs, the cleanup records the
 * failure and runs the next steps.
 *
 * Steps run on [Dispatchers.Default], so [stepTimeout] counts real time also when the test body runs
 * in the virtual time of `runTest`.
 *
 * ```kotlin
 * val cleanup = GrpcClientTestCleanup()
 * cleanup.run { client.shutdown() }
 * cleanup.run { client.awaitTermination(5.seconds) }
 * cleanup.failure()?.let { throw it }
 * ```
 *
 * @param stepTimeout Real-time limit of each step. The cleanup records a step over the limit as failed.
 */
internal class GrpcClientTestCleanup(
    private val stepTimeout: Duration = 3.seconds,
) {
    private val failures = mutableListOf<Throwable>()

    /**
     * Runs one teardown step with the `stepTimeout` limit.
     *
     * Never throws. The cleanup records each failure of [block], its timeout included, and [failure] reports it.
     */
    internal suspend fun run(block: suspend () -> Unit) {
        try {
            withContext(Dispatchers.Default) {
                withTimeout(stepTimeout) { block() }
            }
        } catch (error: Throwable) {
            failures += error
        }
    }

    /** Returns the first recorded failure with the later ones suppressed, or `null` if every step succeeded. */
    internal fun failure(): Throwable? {
        val first = failures.firstOrNull() ?: return null
        failures.drop(1).forEach(first::addSuppressed)
        return first
    }
}

/** Creates a call id from 128 random bits, unique across all tests that share the reference server. */
private fun newCallId(): String = "grpc-client-test-${Random.nextBytes(16).toHexString()}"
