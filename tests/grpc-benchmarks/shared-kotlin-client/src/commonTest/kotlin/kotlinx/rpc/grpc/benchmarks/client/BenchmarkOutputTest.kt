/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.nanoseconds

class BenchmarkOutputTest {
    @Test
    fun formatsApplicationThroughput() {
        assertEquals("999.00 B/s", 999.0.formatBytesPerSecond())
        assertEquals("1.50 KB/s", 1_500.0.formatBytesPerSecond())
        assertEquals("2.50 MB/s", 2_500_000.0.formatBytesPerSecond())
        assertEquals("3.50 GB/s", 3_500_000_000.0.formatBytesPerSecond())
    }

    @Test
    fun includesImplementationNameInCsvOutput() {
        val parameters = BenchmarkParameters(0, 1, 1, 0, 0)
        val result = BenchmarkResult(
            benchmarkName = "example",
            implementationName = "current",
            platform = "ios",
            parameters = parameters,
            elapsed = 1.nanoseconds,
            applicationBytes = 0,
            latency = LatencyStatistics.from(longArrayOf(1)),
        )

        val output = render(listOf(result), "localhost:50051", OutputFormat.CSV)

        assertContains(output, "benchmark,implementation,platform")
        assertContains(output, "example,current,ios")
    }
}
