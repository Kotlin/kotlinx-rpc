/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CallBenchmarkTest {
    // The client never connects in these tests: the readiness probe is skipped because the
    // benchmark harness is exercised directly.
    private val unusedClient = GrpcClient("localhost", 1) { credentials = plaintext() }

    @Test
    fun completesTrialWithOneSamplePerOperation() = runBlocking {
        var executed = 0
        val benchmark = unaryBenchmark { MeasuredCall(1, 2) { executed++ } }

        val result = benchmark.run(unusedClient, benchmark.cases.single(), parameters(benchmark), 0)

        assertEquals(TrialStatus.COMPLETED, result.status)
        assertEquals(10, executed)
        assertEquals(10, result.operations)
        assertEquals(10, result.latencySamplesNanos.size)
        assertEquals(10, result.applicationRequestBytes)
        assertEquals(20, result.applicationResponseBytes)
        assertTrue(result.elapsedNanos > 0)
    }

    @Test
    fun failsTrialAtFirstOperationError() = runBlocking {
        var executed = 0
        val benchmark = unaryBenchmark {
            MeasuredCall(0, 0) {
                executed++
                if (executed == 3) error("connection closed")
            }
        }

        val result = benchmark.run(unusedClient, benchmark.cases.single(), parameters(benchmark), 0)

        assertEquals(TrialStatus.FAILED, result.status)
        assertEquals(2, result.operations)
        assertEquals(2, result.latencySamplesNanos.size)
        assertContains(result.error ?: "", "Measured operation 3 of 10 failed")
        assertContains(result.error ?: "", "connection closed")
    }

    @Test
    fun includesInjectedDelayInEachSampleWindow() = runBlocking {
        val benchmark = unaryBenchmark { MeasuredCall(0, 0) {} }

        val result = benchmark.run(unusedClient, benchmark.cases.single(), parameters(benchmark), injectedDelayMs = 20)

        assertEquals(TrialStatus.COMPLETED, result.status)
        assertTrue(result.elapsedNanos >= 10 * 20_000_000L, "elapsed ${result.elapsedNanos} ns")
        assertTrue(result.latencySamplesNanos.all { it >= 20_000_000L })
    }

    private fun parameters(benchmark: CallBenchmark): BenchmarkParameters = benchmark.cases.single().parameters

    private fun unaryBenchmark(prepare: () -> MeasuredCall): CallBenchmark =
        CallBenchmark(
            name = "test-unary",
            description = "Test benchmark",
            cases = listOf(benchmarkCase(warmupOperations = 0, operations = 10, concurrency = 1, requestBytes = 0, responseBytes = 0)),
            prepare = { _, _ -> prepare() },
        )
}
