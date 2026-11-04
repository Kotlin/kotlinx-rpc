/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertEquals

class StreamingBenchmarksTest {
    private val registry = BenchmarkRegistry(streamingBenchmarks(TestBenchmarkBackend))

    @Test
    fun definesStreamingBenchmarkFamilies() {
        assertEquals(
            listOf(
                "bidi-full-duplex",
                "bidi-ping-pong",
                "client-streaming-throughput",
                "server-streaming-throughput",
                "stream-concurrency-sweep",
                "stream-message-overhead",
            ),
            registry.all.map(Benchmark::name),
        )
    }

    @Test
    fun definesLatencyMetricPerFamily() {
        assertEquals(
            LatencyMetric.MESSAGE_INTERARRIVAL,
            requireNotNull(registry.find("server-streaming-throughput")).latencyMetric,
        )
        assertEquals(
            LatencyMetric.REQUEST_WRITE_SUSPENSION,
            requireNotNull(registry.find("client-streaming-throughput")).latencyMetric,
        )
        assertEquals(
            LatencyMetric.PING_PONG_ROUND_TRIP,
            requireNotNull(registry.find("bidi-ping-pong")).latencyMetric,
        )
        assertEquals(
            LatencyMetric.MESSAGE_INTERARRIVAL,
            requireNotNull(registry.find("bidi-full-duplex")).latencyMetric,
        )
        for (benchmark in registry.all) {
            assertEquals(OperationUnit.MESSAGE, benchmark.operationUnit, benchmark.name)
        }
    }

    @Test
    fun isolatesMeasuredDirectionInThroughputCases() {
        assertEquals(
            BenchmarkParameters(20, 2_000, 1, 64, 64 * 1_024),
            requireNotNull(registry.find("server-streaming-throughput")).cases.single { it.name == "64k" }.parameters,
        )
        assertEquals(
            BenchmarkParameters(20, 2_000, 1, 64 * 1_024, 64),
            requireNotNull(registry.find("client-streaming-throughput")).cases.single { it.name == "64k" }.parameters,
        )
    }

    @Test
    fun definesFixedTotalMessageOverheadCasesBelowLegacyLimit() {
        val cases = requireNotNull(registry.find("stream-message-overhead")).cases

        assertEquals(listOf("1k", "64k", "1m", "near-4m"), cases.map(BenchmarkCase::name))
        assertEquals(
            BenchmarkParameters(1, 17, 1, 4 * 1_024 * 1_024 - 1_024, 0),
            cases.last().parameters,
        )
        assertEquals(64L * 1_024 * 1_024, cases.first().parameters.operations.toLong() * 1_024)
        assertEquals(
            64L * 1_024 * 1_024,
            cases.last().parameters.requestBytes.toLong() * (cases.last().parameters.operations - 1) + 16 * 1_024,
        )
    }

    @Test
    fun definesStreamConcurrencyCases() {
        val cases = requireNotNull(registry.find("stream-concurrency-sweep")).cases

        assertEquals(listOf("c1", "c2", "c4", "c8", "c16"), cases.map(BenchmarkCase::name))
        assertEquals(BenchmarkParameters(200, 16_384, 16, 64, 1_024), cases.last().parameters)
    }
}
