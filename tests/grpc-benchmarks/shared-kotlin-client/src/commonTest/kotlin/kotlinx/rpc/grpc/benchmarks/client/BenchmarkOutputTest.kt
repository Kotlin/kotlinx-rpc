/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

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
            caseName = "example-case",
            implementationName = "current",
            platform = "ios",
            parameters = parameters,
            elapsed = 1.nanoseconds,
            applicationBytes = 0,
            latency = LatencyStatistics.from(longArrayOf(1)),
        )

        val output = render(listOf(result), "localhost:50051", OutputFormat.CSV)

        assertContains(output, "benchmark,case,implementation,platform")
        assertContains(output, "example,example-case,current,ios")
    }

    @Test
    fun includesStreamingMeasurementsInOutput() {
        val result = BenchmarkResult(
            benchmarkName = "streaming-example",
            caseName = "default",
            implementationName = "current",
            platform = "ios",
            parameters = BenchmarkParameters(0, 10, 1, 1_024, 64),
            elapsed = 1.seconds,
            applicationBytes = 10_304,
            latency = LatencyStatistics.from(longArrayOf(1)),
            requestMessages = 10,
            responseMessages = 1,
            timeToFirstResponse = 2.nanoseconds,
            finalResponseLatency = 3.nanoseconds,
        )

        val output = render(listOf(result), "localhost:50051", OutputFormat.CSV)

        assertContains(output, "request_messages,response_messages,messages_per_second")
        assertContains(output, ",10,1,11.0000,0.002,0.003")
    }
}
