/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BenchmarkOutputTest {
    @Test
    fun formatsApplicationThroughput() {
        assertEquals("999.00 B/s", 999.0.formatBytesPerSecond())
        assertEquals("1.50 KB/s", 1_500.0.formatBytesPerSecond())
        assertEquals("2.50 MB/s", 2_500_000.0.formatBytesPerSecond())
        assertEquals("3.50 GB/s", 3_500_000_000.0.formatBytesPerSecond())
    }

    @Test
    fun writesCsvHeaderWithMetricKind() {
        val output = render(listOf(unaryRecord()), OutputFormat.CSV)

        assertContains(output, "schema_version,implementation,platform,benchmark,case,status,latency_metric")
        assertContains(output, "call_round_trip,rpc")
    }

    @Test
    fun writesStreamingCsvWithDirectionRates() {
        val record = streamingRecord()

        val output = render(listOf(record), OutputFormat.CSV)

        assertContains(output, "request_messages_per_second,response_messages_per_second")
        assertContains(output, "1,100,1.0000,100.0000")
        assertContains(output, "64.0000,102400.0000")
    }

    @Test
    fun quotesCsvFieldsWithSeparators() {
        val record = unaryRecord(error = "call failed: expected 2, got \"1\"")

        val output = render(listOf(record), OutputFormat.CSV)

        val expected = "\"call failed: expected 2, got \"\"1\"\"\""
        assertContains(output, expected)
    }

    @Test
    fun writesJsonRecordsAsOneObjectPerLine() {
        val records = listOf(unaryRecord(), streamingRecord())

        val output = render(records, OutputFormat.JSON)

        assertEquals(2, output.lines().size)
        assertContains(output, """"benchmark":"unary-latency"""")
        assertContains(output, """"benchmark":"server-streaming-throughput"""")
    }

    private fun unaryRecord(error: String? = null): TrialRecord = TrialRecord(
        implementationName = "current",
        implementationVersion = "workspace",
        platform = "ios",
        target = "localhost:50051",
        startedAtEpochMs = 1_762_268_000_000,
        connectionReadyNanos = 5_000_000,
        trial = TrialResult(
            benchmarkName = "unary-latency",
            caseName = "default",
            parameters = BenchmarkParameters(10, 100, 1, 0, 0),
            latencyMetric = LatencyMetric.CALL_ROUND_TRIP,
            operationUnit = OperationUnit.RPC,
            injectedDelayMs = 0,
            status = if (error == null) TrialStatus.COMPLETED else TrialStatus.FAILED,
            error = error,
            elapsedNanos = 1_000_000_000,
            operations = 100,
            requestMessages = null,
            responseMessages = null,
            applicationRequestBytes = 0,
            applicationResponseBytes = 0,
            latencySamplesNanos = longArrayOf(1_000, 2_000, 3_000),
        ),
    )

    private fun streamingRecord(): TrialRecord = TrialRecord(
        implementationName = "current",
        implementationVersion = "workspace",
        platform = "ios",
        target = "localhost:50051",
        startedAtEpochMs = 1_762_268_000_000,
        connectionReadyNanos = 5_000_000,
        trial = TrialResult(
            benchmarkName = "server-streaming-throughput",
            caseName = "1k",
            parameters = BenchmarkParameters(10, 100, 1, 64, 1_024),
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
            latencySamplesNanos = longArrayOf(1_000, 2_000),
            timeToFirstResponseNanos = listOf(500),
            finalResponseLatencyNanos = listOf(999_000_000),
        ),
    )
}
