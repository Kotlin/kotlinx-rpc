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
 * @param clientConfig Additional configuration for the data-plane client.
 * @param configureScenario Whether to configure server-side tracing for the data-plane call.
 * @param barriers Reference-server barriers configured before the data-plane call starts.
 * @param scenario Additional reference-server scenario configuration.
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
    val failure = testFailure
    if (failure != null) {
        cleanupFailure?.let(failure::addSuppressed)
        throw failure
    }
    cleanupFailure?.let { throw it }
}
