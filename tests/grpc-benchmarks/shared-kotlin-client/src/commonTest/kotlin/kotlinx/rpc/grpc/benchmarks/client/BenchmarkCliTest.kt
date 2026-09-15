/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import com.github.ajalt.clikt.testing.test
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.flow.Flow
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

    @Test
    fun rendersRunningBenchmarkProgress() {
        assertEquals(
            "[=====>--------------] 2/8 complete, 6 remaining | " +
                "platform=ios implementation=current | " +
                "running benchmark=unary-payload-sweep case=symmetric-1m",
            renderBenchmarkProgress(
                completed = 2,
                total = 8,
                platform = "ios",
                implementation = "current",
                benchmark = "unary-payload-sweep",
                benchmarkCase = "symmetric-1m",
            ),
        )
    }

    @Test
    fun rendersCompletedBenchmarkProgress() {
        assertEquals(
            "[====================] 8/8 complete, 0 remaining | " +
                "platform=ios implementation=current | finished",
            renderBenchmarkProgress(
                completed = 8,
                total = 8,
                platform = "ios",
                implementation = "current",
            ),
        )
    }

    @Test
    fun rendersProgressUpdatesOnOneTerminalLine() {
        assertEquals(
            "progress",
            renderBenchmarkProgressUpdate(progress = "progress", previousLength = 0, finished = false),
        )
        assertEquals(
            "\r        \rprogress",
            renderBenchmarkProgressUpdate(progress = "progress", previousLength = 8, finished = false),
        )
        assertEquals(
            "\r        \rfinished\n",
            renderBenchmarkProgressUpdate(progress = "finished", previousLength = 8, finished = true),
        )
    }

    @Test
    fun startsNonProgressOutputOnTheLineAfterActiveProgress() {
        assertEquals("", renderBenchmarkProgressLineBreak(previousLength = 0))
        assertEquals("\n", renderBenchmarkProgressLineBreak(previousLength = 8))
    }

    private object TestBenchmarkBackend : BenchmarkBackend {
        override val implementationName: String = "test"

        override fun prepareUnaryCall(client: GrpcClient): suspend (SimpleRequest) -> SimpleResponse =
            error("Benchmark execution is not expected in CLI parsing tests")

        override fun preparePingPongCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
            error("Benchmark execution is not expected in CLI parsing tests")

        override fun prepareClientStreamingCall(client: GrpcClient): suspend (Flow<SimpleRequest>) -> SimpleResponse =
            error("Benchmark execution is not expected in CLI parsing tests")

        override fun prepareServerStreamingCall(client: GrpcClient): (SimpleRequest) -> Flow<SimpleResponse> =
            error("Benchmark execution is not expected in CLI parsing tests")

        override fun prepareFullDuplexCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
            error("Benchmark execution is not expected in CLI parsing tests")
    }
}
