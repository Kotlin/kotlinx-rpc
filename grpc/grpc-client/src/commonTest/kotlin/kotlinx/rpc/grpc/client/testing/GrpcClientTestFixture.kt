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
import kxrpc.testing.GrantInboundDemandRequest
import kxrpc.testing.GrpcClientControlService
import kxrpc.testing.MalformedResponseService
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
 * Owns the data and control clients for one isolated reference-server scenario.
 *
 * A fixture holds two independent clients to [GrpcClientTestServer]:
 * - The **data-plane client** runs the calls under test through [testService], [unimplementedService], and
 *   [malformedResponseService]. It is configured with [clientConfig] and attaches [callId] to every call, so the
 *   server applies this fixture's scenario to them and records their lifecycle.
 * - The **control-plane client** calls `GrpcClientControlService` to configure the scenario, synchronize with the
 *   server ([awaitServerEvent], [releaseServerBarrier], [grantInboundDemand]), and read the server's trace. It
 *   does not use [clientConfig], does not attach the call id, and stays usable when a test shuts down the
 *   data-plane client.
 *
 * Every fixture uses a fresh random [callId], so tests running concurrently against the shared server, for
 * example JVM and native test tasks, never see each other's calls.
 *
 * Tests normally obtain a fixture through [grpcClientTest], which also registers the scenario and calls [close].
 *
 * @param clientConfig Additional configuration applied to the data-plane client.
 */
internal class GrpcClientTestFixture(
    private val clientConfig: GrpcClientConfiguration.() -> Unit,
) {
    /**
     * Unique metadata identifier used to correlate this test with its server-side trace.
     *
     * Sent as [GrpcClientTestServer.CALL_ID_METADATA_KEY] on every data-plane call and passed to every
     * control-plane call.
     */
    internal val callId: String = newCallId()

    private val dataClient: GrpcClient = createDataClient(GrpcClientTestServer.PORT)
    private val controlClient: GrpcClient = GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.PORT) {
        credentials = plaintext()
    }
    private val controlService: GrpcClientControlService = controlClient.withService()

    /**
     * Generated upstream interoperability service used for data-plane calls.
     *
     * This is the official `grpc.testing.TestService`, implemented by the server like grpc-java's interop server.
     */
    internal val testService: TestService = dataClient.withService()

    /**
     * Generated upstream service which is intentionally absent from the reference server.
     *
     * Every call fails with `UNIMPLEMENTED` before the server applies a scenario, so tests using it pass
     * `configureScenario = false` to [grpcClientTest].
     */
    internal val unimplementedService: UnimplementedService = dataClient.withService()

    /**
     * Generated client for deliberately invalid response-cardinality behavior.
     *
     * The server answers its unary and client-streaming methods with zero or two responses, as selected by the
     * scenario, which a conforming server cannot do. Calls always need a configured scenario.
     */
    internal val malformedResponseService: MalformedResponseService = dataClient.withService()

    // Set before ConfigureScenario is sent, so teardown also discards a scenario that the server
    // registered although the client saw the call fail.
    private var scenarioSetupAttempted: Boolean = false

    // Set once ConfigureScenario succeeded; a successful test then checks that the scenario ended cleanly.
    private var scenarioConfigured: Boolean = false
    private var disposableEndpointStarted: Boolean = false
    private var disposableDataClient: GrpcClient? = null

    /**
     * Registers this fixture's scenario before its data-plane call starts.
     *
     * Sends `ConfigureScenario` for [callId] with [barriers] and any further settings from [configure], such as
     * terminal behavior, response metadata, or flow control. Each call id can be configured once, so this must be
     * called at most once per fixture; [grpcClientTest] calls it unless `configureScenario = false`.
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
     * Waits until the server records the requested one-based event occurrence.
     *
     * Returns immediately if the event has already been recorded. Use it to wait until the server has reached a
     * certain point of the call, for example `awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT, 2U)` returns once
     * the server has sent its second response. The server gives up after 10 seconds with `DEADLINE_EXCEEDED`.
     *
     * @return The recorded event, including its metadata and request payload.
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
     * Releases one configured one-based reference-server barrier occurrence.
     *
     * The server continues the paused call, or passes the barrier without stopping if it has not reached it yet.
     * Each barrier occurrence can be released once. Every configured barrier must be released before the test
     * ends, otherwise teardown reports it as leaked.
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
     * Grants the reference server demand for additional streaming request messages.
     *
     * Only valid for scenarios configured with manual inbound demand. There, the server reads no request message
     * until the test grants demand, and then reads at most [messageCount] further messages. Requests which are not
     * read stay in the transport, so a test can observe the client under backpressure.
     */
    internal suspend fun grantInboundDemand(messageCount: UInt = 1U) {
        require(messageCount > 0U) { "inbound demand must be positive" }
        controlService.grantInboundDemand(
            GrantInboundDemandRequest {
                callId = this@GrpcClientTestFixture.callId
                this.messageCount = messageCount
            }
        )
    }

    /**
     * Starts and returns a service on an isolated listener which tests may terminate remotely.
     *
     * The server starts a separate listener for this scenario on a free port, and the fixture creates a second
     * data-plane client for it, configured like the main one. [stopDisposableEndpoint] later resets every TCP
     * connection of that listener, which simulates a lost connection without affecting other tests. Can be called
     * once per fixture; [close] stops the listener and shuts down its client.
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
     * Abruptly terminates the isolated listener and every data-plane call using it.
     *
     * The server resets all TCP connections of the listener started by [startDisposableTestService], so calls on
     * it fail like calls on a lost connection.
     */
    internal suspend fun stopDisposableEndpoint() {
        check(disposableEndpointStarted) { "disposable endpoint is not started" }
        controlService.stopDisposableEndpoint(
            StopDisposableEndpointRequest { callId = this@GrpcClientTestFixture.callId }
        )
        disposableEndpointStarted = false
    }

    /**
     * Verifies an event is absent from the current trace, then releases its protecting barrier.
     *
     * [barrier] must be configured so that it holds back [event], for example a [BarrierType.SEND_RESPONSE]
     * barrier for an [EventType.RESPONSE_MESSAGE_SENT]. Because the server cannot pass the barrier, the absence
     * check cannot race with the event; it fails if the event was recorded anyway. Afterwards the barrier is
     * released, and the event may follow.
     */
    internal suspend fun assertServerEventAbsentUntil(
        event: EventType,
        barrier: BarrierType,
        occurrence: UInt = 1U,
    ) {
        val trace = serverTrace()
        assertNull(
            trace.events.firstOrNull { it.type == event && it.occurrence == occurrence },
            "call_id='$callId', unexpectedly observed $event occurrence $occurrence:\n${trace.render()}",
        )
        releaseServerBarrier(barrier, occurrence)
    }

    /**
     * Returns the server's ordered event trace for this fixture.
     *
     * The trace is a snapshot: events of a call that is still running may follow later.
     */
    internal suspend fun serverTrace(): CallTrace {
        return controlService.getTrace(GetTraceRequest { callId = this@GrpcClientTestFixture.callId })
    }

    /** Returns server-side lifecycle and barrier state for teardown diagnostics. */
    internal suspend fun serverDiagnostics(): ScenarioDiagnostics {
        return controlService.getScenarioDiagnostics(
            GetScenarioDiagnosticsRequest { callId = this@GrpcClientTestFixture.callId }
        )
    }

    /**
     * Awaits terminal server closure and verifies the complete ordered trace.
     *
     * Waits for [EventType.CALL_CLOSED] first, so the trace is complete when it is compared with
     * [expectedEvents], see [assertEvents]. For calls the client cancelled, use [assertServerCancelledTrace].
     */
    internal suspend fun assertServerTrace(expectedEvents: List<ExpectedServerEvent>) {
        awaitServerEvent(EventType.CALL_CLOSED)
        serverTrace().assertEvents(callId, expectedEvents)
    }

    /**
     * Awaits server-observed client cancellation and verifies the complete ordered trace.
     *
     * The cancellation reaches the server asynchronously, so this waits for [EventType.CLIENT_CANCELLED] before
     * comparing the trace with [expectedEvents], see [assertEvents].
     */
    internal suspend fun assertServerCancelledTrace(expectedEvents: List<ExpectedServerEvent>) {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        serverTrace().assertEvents(callId, expectedEvents)
    }

    /**
     * Awaits cancellation and accepts one of several explicitly documented event orderings.
     *
     * Like [assertServerCancelledTrace], but the trace may match any of [expectedAlternatives], see
     * [assertEventsOneOf].
     */
    internal suspend fun assertServerCancelledTraceOneOf(
        expectedAlternatives: List<List<ExpectedServerEvent>>,
    ) {
        awaitServerEvent(EventType.CLIENT_CANCELLED)
        serverTrace().assertEventsOneOf(callId, expectedAlternatives)
    }

    /**
     * Requires one server-observed cancellation and no competing server close.
     *
     * Waits for [EventType.CLIENT_CANCELLED] and checks that it is recorded exactly once, as the last event,
     * and that the server never closed the call itself. Unlike [assertServerCancelledTrace], the events before
     * the cancellation are not checked.
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
     * Accepts either server close or peer cancellation after an exact lifecycle prefix.
     *
     * Does not wait for the terminal event: call it only once the server has ended the call. See
     * [assertEventsWithEitherTerminal].
     */
    internal suspend fun assertServerTraceWithEitherTerminal(
        expectedBeforeTerminal: List<ExpectedServerEvent>,
    ) {
        serverTrace().assertEventsWithEitherTerminal(callId, expectedBeforeTerminal)
    }

    /**
     * Compares response payloads with call and server-trace context on failure.
     *
     * Same check as [assertPayloadSequence]; the failure message additionally contains [callId] and the current
     * server trace.
     */
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
     * Verifies the exact ordered request payload bytes observed by the reference server.
     *
     * The server records the payload of every interop request it receives on its
     * [EventType.REQUEST_MESSAGE_RECEIVED] event, so this checks what actually arrived, independently of what the
     * client reports. Does not wait for further requests: call it once the server has received them all.
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

    /**
     * Verifies the complete request-to-close lifecycle of one successful unary call.
     *
     * Shorthand for [assertServerTrace] with [successfulUnaryEvents].
     */
    internal suspend fun assertUnaryLifecycle() {
        assertServerTrace(successfulUnaryEvents())
    }

    /**
     * Forcefully shuts down the data-plane client while leaving the control channel available.
     *
     * Cancels the client's running calls; the test can still inspect the server through the control client.
     */
    internal fun shutdownDataClientNow() {
        dataClient.shutdownNow()
    }

    /**
     * Begins graceful data-plane client shutdown.
     *
     * Running calls may complete, new calls are rejected. The control client is not affected.
     */
    internal fun shutdownDataClient() {
        dataClient.shutdown()
    }

    /** Waits for the data-plane client to terminate, bounded by [duration]. */
    internal suspend fun awaitDataClientTermination(duration: Duration = 5.seconds) {
        dataClient.awaitTermination(duration)
    }

    /**
     * Discards server state and closes both clients, preserving any cleanup failure.
     *
     * Every step runs through [GrpcClientTestCleanup], so a failing or hanging step does not skip the rest.
     *
     * After a successful test body, teardown also verifies the scenario: every data-plane call must have ended,
     * and the server must hold no open calls, barriers, or waiters (see [assertNoLeaks]). The clients are then shut
     * down gracefully.
     *
     * After a failed test body, the data-plane clients are shut down forcefully first, since calls may still be
     * paused at barriers. The server trace and diagnostics are then printed to help diagnose the failure, without
     * further assertions.
     *
     * In both cases the scenario is discarded on the server if its setup was attempted, which also stops a
     * disposable endpoint and fails control calls still waiting on it. Finally, all clients are awaited to
     * terminate.
     *
     * @param testFailure The test-body failure, or `null` after success.
     * @return The first cleanup failure with any additional failures suppressed.
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
            }
        }

        cleanup.run { controlClient.shutdown() }
        cleanup.run { dataClient.awaitTermination(5.seconds) }
        cleanup.run { disposableDataClient?.awaitTermination(5.seconds) }
        cleanup.run { controlClient.awaitTermination(5.seconds) }

        return cleanup.failure()
    }

    /**
     * Checks that the scenario ended cleanly after a successful test body.
     *
     * If no call has ended yet, waits for [EventType.CALL_CLOSED]. Then every accepted data-plane call must have
     * been closed or cancelled, the trace must end with such a terminal event, and the server must hold no
     * leftover state for the scenario.
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
     * [CallIdInterceptor] is added last, after any interceptors from [clientConfig], so it is the last
     * interceptor to run before the transport.
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

/** Opts every data-plane call into the scenario of [callId] by appending the call-id request metadata. */
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
 * Runs teardown steps independently of each other: a failing or hanging step is recorded, and the
 * following steps still run.
 *
 * Steps run on [Dispatchers.Default], so [stepTimeout] is measured in real time even when the
 * test body runs under `runTest`'s virtual time.
 *
 * ```kotlin
 * val cleanup = GrpcClientTestCleanup()
 * cleanup.run { client.shutdown() }
 * cleanup.run { client.awaitTermination(5.seconds) }
 * cleanup.failure()?.let { throw it }
 * ```
 *
 * @param stepTimeout Real-time limit for each step; a step exceeding it is recorded as failed.
 */
internal class GrpcClientTestCleanup(
    private val stepTimeout: Duration = 3.seconds,
) {
    private val failures = mutableListOf<Throwable>()

    /**
     * Runs one teardown step, bounded by `stepTimeout`.
     *
     * Never throws: any failure of [block], including its timeout, is recorded and reported by [failure].
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

/** Creates a call id from 128 random bits, unique across all tests sharing the reference server. */
private fun newCallId(): String = "grpc-client-test-${Random.nextBytes(16).toHexString()}"
