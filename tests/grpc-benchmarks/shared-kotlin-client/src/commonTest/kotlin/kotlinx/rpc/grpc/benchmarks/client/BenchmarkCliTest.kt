/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import com.github.ajalt.clikt.testing.test
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BenchmarkCliTest {
    @Test
    fun listsBenchmarks() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("list")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, "unary-latency")
        assertContains(result.stdout, "unary-throughput")
        assertContains(result.stdout, "unary-payload-sweep")
        assertContains(result.stdout, "unary-concurrency-sweep")
        assertContains(result.stdout, "symmetric-1m")
        assertContains(result.stdout, "1k-c128")
    }

    @Test
    fun showsHelpCommand() {
        val rootHelp = benchmarkCommand(TestBenchmarkBackend).test("help")
        val runHelp = benchmarkCommand(TestBenchmarkBackend).test("help run")

        assertEquals(0, rootHelp.statusCode)
        assertContains(rootHelp.stdout, "Commands:")
        assertContains(rootHelp.stdout, "help")
        assertContains(rootHelp.stdout, "list")
        assertContains(rootHelp.stdout, "run")
        assertEquals(0, runHelp.statusCode)
        assertContains(runHelp.stdout, "--target")
        assertContains(runHelp.stdout, "--concurrency")
    }

    @Test
    fun describesRunOptions() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run --help")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, "--target")
        assertContains(result.stdout, "--concurrency")
        assertContains(result.stdout, "--case")
        assertContains(result.stdout, "--request-bytes")
        assertContains(result.stdout, "--case")
        assertContains(result.stdout, "--format")
    }

    @Test
    fun rejectsInvalidNumbers() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run unary-latency --calls many")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "--calls")
        assertContains(result.stderr, "invalid", ignoreCase = true)
    }

    @Test
    fun rejectsUnknownBenchmarks() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run unknown")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "registered benchmark")
    }

    @Test
    fun rejectsUnknownBenchmarkCases() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run unary-payload-sweep --case missing")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "has no case 'missing'")
    }

    @Test
    fun rejectsCaseSelectionForAllBenchmarks() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run all --case default")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "requires a specific benchmark")
    }

    private object TestBenchmarkBackend : BenchmarkBackend {
        override val implementationName: String = "test"

        override fun prepareUnaryCall(client: GrpcClient): suspend (SimpleRequest) -> SimpleResponse =
            error("Benchmark execution is not expected in CLI parsing tests")
    }
}
