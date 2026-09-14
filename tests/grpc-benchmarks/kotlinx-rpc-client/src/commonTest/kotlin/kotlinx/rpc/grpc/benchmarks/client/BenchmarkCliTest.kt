/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.nanoseconds

class BenchmarkCliTest {
    @Test
    fun formatsApplicationThroughput() {
        assertEquals("999.00 B/s", 999.0.formatBytesPerSecond())
        assertEquals("1.50 KB/s", 1_500.0.formatBytesPerSecond())
        assertEquals("2.50 MB/s", 2_500_000.0.formatBytesPerSecond())
        assertEquals("3.50 GB/s", 3_500_000_000.0.formatBytesPerSecond())
    }

    @Test
    fun appliesParameterOverrides() {
        val defaults = BenchmarkParameters(1, 2, 3, 4, 5)

        assertEquals(
            BenchmarkParameters(1, 20, 3, 4, 50),
            BenchmarkOverrides(calls = 20, responseBytes = 50).applyTo(defaults),
        )
        assertFailsWith<IllegalArgumentException> {
            BenchmarkOverrides(concurrency = 0).applyTo(defaults)
        }
    }

    @Test
    fun listsBenchmarks() {
        val result = benchmarkCommand().test("list")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, "unary-latency")
        assertContains(result.stdout, "unary-throughput")
    }

    @Test
    fun showsHelpCommand() {
        val rootHelp = benchmarkCommand().test("help")
        val runHelp = benchmarkCommand().test("help run")

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
        val result = benchmarkCommand().test("run --help")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, "--target")
        assertContains(result.stdout, "--concurrency")
        assertContains(result.stdout, "--request-bytes")
        assertContains(result.stdout, "--format")
    }

    @Test
    fun rejectsInvalidNumbers() {
        val result = benchmarkCommand().test("run unary-latency --calls many")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "--calls")
        assertContains(result.stderr, "invalid", ignoreCase = true)
    }

    @Test
    fun rejectsUnknownBenchmarks() {
        val result = benchmarkCommand().test("run unknown")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "registered benchmark")
    }

    @Test
    fun computesLatencyPercentiles() {
        val statistics = LatencyStatistics.from(longArrayOf(1, 2, 3, 4, 100))

        assertEquals(1.nanoseconds, statistics.minimum)
        assertEquals(22.nanoseconds, statistics.mean)
        assertEquals(3.nanoseconds, statistics.p50)
        assertEquals(100.nanoseconds, statistics.p90)
        assertEquals(100.nanoseconds, statistics.maximum)
    }
}
