/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.TestResult
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kxrpc.testing.Barrier
import kxrpc.testing.ConfigureScenarioRequest
import kotlin.time.Duration.Companion.seconds

/**
 * Runs a common gRPC client test with bounded execution and guaranteed fixture cleanup.
 *
 * Every invocation gets its own [GrpcClientTestFixture]: a data-plane client for the calls under test and a
 * separate control-plane client for the reference server, both connected to [GrpcClientTestServer]. Unless
 * [configureScenario] is `false`, the fixture registers a scenario under its fresh [GrpcClientTestFixture.callId]
 * before [block] runs, so the server traces every data-plane call of the test and applies [barriers] and
 * [scenario] to it.
 *
 * The test runs under `runTest` with coroutine debug probes and a 30-second timeout. Whether [block] succeeds or
 * fails, the fixture is closed afterwards (see [GrpcClientTestFixture.close]); a failure of [block] is rethrown
 * with any cleanup failure suppressed, see [combineTestAndCleanupFailures].
 *
 * ```kotlin
 * @Test
 * fun holdsSecondResponse() = grpcClientTest(
 *     barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
 * ) {
 *     val responses = async { testService.streamingOutputCall(request).toList() }
 *     awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)
 *     releaseServerBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)
 *     responses.await()
 *     assertServerTrace(successfulServerStreamingEvents(responseCount = 2))
 * }
 * ```
 *
 * @param clientConfig Additional configuration for the data-plane client, applied before the call-id interceptor.
 * @param configureScenario Whether to register a scenario for the data-plane calls. Pass `false` only for calls
 *   which never reach a scenario-aware service, such as calls to [GrpcClientTestFixture.unimplementedService]:
 *   the data-plane client always attaches the call id, and the server rejects scenario-aware calls whose call id
 *   is not registered.
 * @param barriers Reference-server barriers configured before the data-plane call starts.
 *   Requires [configureScenario].
 * @param scenario Additional reference-server scenario configuration, such as terminal behavior or metadata.
 * @param block Test body executed with a configured [GrpcClientTestFixture].
 * @return The platform-specific coroutine test result.
 */
internal fun grpcClientTest(
    clientConfig: GrpcClientConfiguration.() -> Unit = {},
    configureScenario: Boolean = true,
    barriers: List<Barrier> = emptyList(),
    scenario: ConfigureScenarioRequest.Builder.() -> Unit = {},
    block: suspend GrpcClientTestFixture.() -> Unit,
): TestResult = runTestWithCoroutinesProbes(timeout = 30.seconds) {
    executeGrpcClientTest(clientConfig, configureScenario, barriers, scenario, block)
}

/**
 * The body of [grpcClientTest] without the surrounding `runTest`, so a test can run it in its own coroutine
 * test and inspect the failure it throws.
 *
 * Creates the fixture, registers the scenario if [configureScenario] is set, and runs [block]. Any failure of
 * scenario setup or [block] is caught, the fixture is closed under [NonCancellable] (so teardown also runs when
 * the test was cancelled, for example by its timeout), and the combined failure is rethrown.
 *
 * Parameters are the same as for [grpcClientTest].
 */
internal suspend fun executeGrpcClientTest(
    clientConfig: GrpcClientConfiguration.() -> Unit = {},
    configureScenario: Boolean = true,
    barriers: List<Barrier> = emptyList(),
    scenario: ConfigureScenarioRequest.Builder.() -> Unit = {},
    block: suspend GrpcClientTestFixture.() -> Unit,
) {
    require(configureScenario || barriers.isEmpty()) {
        "server barriers require a configured scenario"
    }
    val fixture = GrpcClientTestFixture(clientConfig)
    var testFailure: Throwable? = null

    try {
        if (configureScenario) fixture.configureScenario(barriers, scenario)
        fixture.block()
    } catch (error: Throwable) {
        testFailure = error
    }

    val cleanupFailure = withContext(NonCancellable) { fixture.close(testFailure) }
    combineTestAndCleanupFailures(testFailure, cleanupFailure)?.let { throw it }
}

/**
 * Picks the failure a test reports when both its body and its teardown may have failed.
 *
 * The test-body failure takes precedence, because teardown failures are usually a consequence of it; the
 * cleanup failure is attached to it as suppressed, so it still shows up in the report.
 *
 * @return [testFailure] with [cleanupFailure] suppressed, [cleanupFailure] if the body succeeded,
 *   or `null` if neither failed.
 */
internal fun combineTestAndCleanupFailures(
    testFailure: Throwable?,
    cleanupFailure: Throwable?,
): Throwable? {
    if (testFailure == null) return cleanupFailure
    cleanupFailure?.let(testFailure::addSuppressed)
    return testFailure
}
