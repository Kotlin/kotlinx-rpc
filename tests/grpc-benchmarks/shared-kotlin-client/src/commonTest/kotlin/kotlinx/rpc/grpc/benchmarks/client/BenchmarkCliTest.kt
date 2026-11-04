/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun listsBenchmarksAsJsonForTheEquivalenceCheck() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("list --format json")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, """"benchmark": "unary-latency"""")
        assertContains(result.stdout, """"latency_metric": "call_round_trip"""")
        assertContains(result.stdout, """"warmup_operations": 200""")
        assertContains(result.stdout, """"case": "symmetric-1m"""")
    }

    @Test
    fun showsHelpCommand() {
        val rootHelp = benchmarkCommand(TestBenchmarkBackend).test("help")
        val runHelp = benchmarkCommand(TestBenchmarkBackend).test("help run")

        assertEquals(0, rootHelp.statusCode)
        assertContains(rootHelp.stdout, "Commands:")
        assertContains(rootHelp.stdout, "help")
        assertContains(rootHelp.stdout, "list")
        assertContains(rootHelp.stdout, "ping")
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
        assertContains(result.stdout, "--operations")
        assertContains(result.stdout, "--inject-delay-ms")
        assertContains(result.stdout, "--format")
        assertContains(result.stdout, "--platform-label")
    }

    @Test
    fun rejectsInvalidNumbers() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run unary-latency --operations many")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "--operations")
        assertContains(result.stderr, "invalid", ignoreCase = true)
    }

    @Test
    fun rejectsNegativeInjectedDelay() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("run unary-latency --inject-delay-ms -1")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "--inject-delay-ms")
        assertContains(result.stderr, "non-negative")
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
    fun rejectsInvalidPingTarget() {
        val result = benchmarkCommand(TestBenchmarkBackend).test("ping --target no-colon")

        assertEquals(2, result.statusCode)
        assertContains(result.stderr, "no-colon")
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

    @Test
    fun registryJsonMatchesWorkloadMatrixOfBothKotlinClients() {
        val json = encodeJson(BenchmarkRegistry(
            unaryBenchmarks(TestBenchmarkBackend) + streamingBenchmarks(TestBenchmarkBackend),
        ).toJson(), pretty = true)

        assertTrue(json.contains("\"benchmark\": \"bidi-ping-pong\""))
        assertTrue(json.contains("\"latency_metric\": \"ping_pong_round_trip\""))
        assertTrue(json.contains("\"operation_unit\": \"message\""))
    }
}
