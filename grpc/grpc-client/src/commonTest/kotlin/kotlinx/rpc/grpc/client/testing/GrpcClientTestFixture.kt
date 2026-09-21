/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import io.grpc.testing.integration.TestService
import io.grpc.testing.integration.UnimplementedService
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.withService
import kxrpc.testing.*
import kotlin.random.Random
import kotlin.test.assertEquals
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

    private var scenarioConfigured: Boolean = false

    /** Registers this fixture's scenario before its data-plane call starts. */
    internal suspend fun configureScenario(
        barriers: List<Barrier> = emptyList(),
        configure: ConfigureScenarioRequest.Builder.() -> Unit = {},
    ) {
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

    /**
     * Discards server state and closes both clients, preserving any cleanup failure.
     *
     * @param testFailure The test-body failure, or `null` after success.
     * @return The first cleanup failure with any additional failures suppressed.
     */
    internal suspend fun close(testFailure: Throwable?): Throwable? {
        val cleanupFailures = mutableListOf<Throwable>()

        if (testFailure == null) {
            if (scenarioConfigured) {
                runCleanup(cleanupFailures) { assertScenarioCompleted() }
            }
            runCleanup(cleanupFailures) { dataClient.shutdown() }
        } else {
            runCleanup(cleanupFailures) { dataClient.shutdownNow() }
            if (scenarioConfigured) {
                runCleanup(cleanupFailures) {
                    println("grpcClientTest failed for call_id='$callId'; server trace:\n${serverTrace().render()}")
                }
                runCleanup(cleanupFailures) {
                    println("grpcClientTest server diagnostics for call_id='$callId': ${serverDiagnostics()}")
                }
            }
        }

        if (scenarioConfigured) {
            runCleanup(cleanupFailures) {
                controlService.discardScenario(DiscardScenarioRequest { callId = this@GrpcClientTestFixture.callId })
                scenarioConfigured = false
            }
        }

        runCleanup(cleanupFailures) { controlClient.shutdown() }
        runCleanup(cleanupFailures) { dataClient.awaitTermination(5.seconds) }
        runCleanup(cleanupFailures) { controlClient.awaitTermination(5.seconds) }

        return cleanupFailures.combine()
    }

    private suspend fun assertScenarioCompleted() {
        awaitServerEvent(EventType.CALL_CLOSED)
        val trace = serverTrace()
        val failureMessage = "call_id='$callId', server trace:\n${trace.render()}"
        assertEquals(1, trace.events.count { it.type == EventType.CALL_CLOSED }, failureMessage)
        assertEquals(EventType.CALL_CLOSED, trace.events.lastOrNull()?.type, failureMessage)
        serverDiagnostics().assertNoLeaks(callId, trace)
    }
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

private suspend fun runCleanup(
    failures: MutableList<Throwable>,
    block: suspend () -> Unit,
) {
    try {
        block()
    } catch (error: Throwable) {
        failures += error
    }
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
