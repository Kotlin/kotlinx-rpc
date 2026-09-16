/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.client.GrpcClient
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
    fun isolatesMeasuredDirectionInThroughputCases() {
        assertEquals(
            BenchmarkParameters(10, 1_000, 1, 64, 64 * 1_024),
            requireNotNull(registry.find("server-streaming-throughput")).cases.single { it.name == "64k" }.parameters,
        )
        assertEquals(
            BenchmarkParameters(10, 1_000, 1, 64 * 1_024, 64),
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
        assertEquals(64L * 1_024 * 1_024, cases.first().parameters.calls.toLong() * 1_024)
        assertEquals(
            64L * 1_024 * 1_024,
            cases.last().parameters.requestBytes.toLong() * (cases.last().parameters.calls - 1) + 16 * 1_024,
        )
    }

    @Test
    fun definesStreamConcurrencyCases() {
        val cases = requireNotNull(registry.find("stream-concurrency-sweep")).cases

        assertEquals(listOf("c1", "c2", "c4", "c8", "c16"), cases.map(BenchmarkCase::name))
        assertEquals(BenchmarkParameters(128, 8_192, 16, 64, 1_024), cases.last().parameters)
    }

    private object TestBenchmarkBackend : BenchmarkBackend {
        override val implementationName: String = "test"

        override fun prepareUnaryCall(client: GrpcClient): suspend (SimpleRequest) -> SimpleResponse = unsupported()

        override fun preparePingPongCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
            unsupported()

        override fun prepareClientStreamingCall(client: GrpcClient): suspend (Flow<SimpleRequest>) -> SimpleResponse =
            unsupported()

        override fun prepareServerStreamingCall(client: GrpcClient): (SimpleRequest) -> Flow<SimpleResponse> =
            unsupported()

        override fun prepareFullDuplexCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
            unsupported()

        private fun unsupported(): Nothing = error("Benchmark execution is not expected in definition tests")
    }
}
