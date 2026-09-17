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
import kotlin.time.Duration.Companion.seconds

/**
 * Runs a common gRPC client test with bounded execution and guaranteed fixture cleanup.
 *
 * @param clientConfig Additional configuration for the data-plane client.
 * @param barriers Reference-server barriers configured before the data-plane call starts.
 * @param block Test body executed with a configured [GrpcClientTestFixture].
 * @return The platform-specific coroutine test result.
 */
internal fun grpcClientTest(
    clientConfig: GrpcClientConfiguration.() -> Unit = {},
    barriers: List<Barrier> = emptyList(),
    block: suspend GrpcClientTestFixture.() -> Unit,
): TestResult = runTestWithCoroutinesProbes(timeout = 30.seconds) {
    val fixture = GrpcClientTestFixture(clientConfig)
    var testFailure: Throwable? = null

    try {
        fixture.configureScenario(barriers)
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
