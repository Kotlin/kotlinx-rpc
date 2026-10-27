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
 * Runs a common gRPC client test against its own reference-server scenario.
 *
 * The test runs under `runTest` with coroutine debug probes and a 30-second timeout.
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
 * @param clientConfig Additional configuration of the data-plane client. Its interceptors run before the call-id
 *   interceptor.
 * @param configureScenario Whether to register a scenario for the data-plane calls. Pass `false` only for calls
 *   that never reach a scenario-aware service, such as calls to [GrpcClientTestFixture.unimplementedService].
 *   The data-plane client always sends the call id, and the server rejects a scenario-aware call with an
 *   unregistered call id.
 * @param barriers Reference-server barriers of the scenario. Requires [configureScenario].
 * @param scenario Additional scenario settings, such as terminal behavior or metadata.
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

internal suspend fun executeGrpcClientTest(
    clientConfig: GrpcClientConfiguration.() -> Unit = {},
    configureScenario: Boolean = true,
    barriers: List<Barrier> = emptyList(),
    scenario: ConfigureScenarioRequest.Builder.() -> Unit = {},
    block: suspend GrpcClientTestFixture.() -> Unit,
) {
    require(configureScenario || barriers.isEmpty()) {
        "server barriers require configureScenario = true"
    }
    val fixture = GrpcClientTestFixture(clientConfig)
    var testFailure: Throwable? = null

    try {
        if (configureScenario) {
            fixture.configureScenario(barriers, scenario)
        }
        fixture.block()
    } catch (error: Throwable) {
        testFailure = error
    }

    val cleanupFailure = withContext(NonCancellable) { fixture.close(testFailure) }
    combineTestAndCleanupFailures(testFailure, cleanupFailure)?.let { throw it }
}

/**
 * Selects the failure that a test reports when its body, its teardown, or both fail.
 *
 * The body failure has priority, because a teardown failure is usually a consequence of it.
 *
 * @return [testFailure] with [cleanupFailure] suppressed, [cleanupFailure] if the body succeeded,
 *   or `null` if neither failed.
 */
internal fun combineTestAndCleanupFailures(
    testFailure: Throwable?,
    cleanupFailure: Throwable?,
): Throwable? {
    if (testFailure == null) {
        return cleanupFailure
    }
    cleanupFailure?.let(testFailure::addSuppressed)
    return testFailure
}
