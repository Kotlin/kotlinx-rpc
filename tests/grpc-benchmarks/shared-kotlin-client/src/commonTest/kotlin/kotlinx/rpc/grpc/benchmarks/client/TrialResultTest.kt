/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.nanoseconds

class TrialResultTest {
    @Test
    fun computesLatencyPercentiles() {
        val statistics = LatencyStatistics.from(longArrayOf(1, 2, 3, 4, 100))

        assertEquals(5, statistics.sampleCount)
        assertEquals(1.nanoseconds, statistics.minimum)
        assertEquals(22.nanoseconds, statistics.mean)
        assertEquals(3.nanoseconds, statistics.p50)
        assertEquals(100.nanoseconds, statistics.p90)
        assertEquals(100.nanoseconds, statistics.maximum)
    }

    @Test
    fun reportsEmptyStatisticsWithoutSamples() {
        val statistics = LatencyStatistics.from(LongArray(0))

        assertEquals(0, statistics.sampleCount)
        assertEquals(null, statistics.minimum)
        assertEquals(null, statistics.p50)
        assertEquals(null, statistics.maximum)
    }

    @Test
    fun encodesTrialRecordJsonWithSamplesAndEvents() {
        val record = TrialRecord(
            implementationName = "current",
            implementationVersion = "workspace",
            platform = "ios-simulator-arm64",
            target = "localhost:50051",
            startedAtEpochMs = 1_762_268_000_000,
            connectionReadyNanos = 123_000_000,
            trial = TrialResult(
                benchmarkName = "server-streaming-throughput",
                caseName = "1k",
                parameters = BenchmarkParameters(10, 100, 2, 64, 1_024),
                latencyMetric = LatencyMetric.MESSAGE_INTERARRIVAL,
                operationUnit = OperationUnit.MESSAGE,
                injectedDelayMs = 0,
                status = TrialStatus.COMPLETED,
                error = null,
                elapsedNanos = 1_000_000_000,
                operations = 101,
                requestMessages = 1,
                responseMessages = 100,
                applicationRequestBytes = 64,
                applicationResponseBytes = 102_400,
                latencySamplesNanos = longArrayOf(1, 2, 3),
                timeToFirstResponseNanos = listOf(500),
                streamDurationNanos = listOf(999_000_000),
            ),
        )

        val json = encodeJson(record.toJson())

        assertTrue(json.startsWith("""{"schema_version":1,"implementation":"current","""), json)
        assertContains(json, """"latency_metric":"message_interarrival"""")
        assertContains(json, """"load_shape":"closed_loop"""")
        assertContains(json, """"connection_ready_ns":123000000""")
        assertContains(json, """"latency_samples_ns":[1,2,3]""")
        assertContains(json, """"time_to_first_response_ns":[500]""")
        assertContains(json, """"stream_duration_ns":[999000000]""")
        assertContains(json, """"final_response_latency_ns":[]""")
    }

    private fun assertContains(text: String, part: String) {
        assertTrue(text.contains(part), "Expected '$part' in:\n$text")
    }
}
