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
        assertContains(result.stdout, "--request-bytes")
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

    private object TestBenchmarkBackend : BenchmarkBackend {
        override val implementationName: String = "test"

        override fun prepareUnaryCall(client: GrpcClient): suspend (SimpleRequest) -> SimpleResponse =
            error("Benchmark execution is not expected in CLI parsing tests")
    }
}
