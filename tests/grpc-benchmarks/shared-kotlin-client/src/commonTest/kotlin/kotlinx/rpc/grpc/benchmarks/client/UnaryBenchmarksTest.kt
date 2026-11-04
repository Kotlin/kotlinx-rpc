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

class UnaryBenchmarksTest {
    private val registry = BenchmarkRegistry(unaryBenchmarks(TestBenchmarkBackend))

    @Test
    fun definesPayloadSweepCases() {
        val cases = requireNotNull(registry.find("unary-payload-sweep")).cases

        assertEquals(
            listOf(
                "symmetric-64b",
                "symmetric-1k",
                "symmetric-64k",
                "symmetric-1m",
                "symmetric-near-4m",
                "upload-1k",
                "upload-64k",
                "upload-1m",
                "upload-near-4m",
                "download-1k",
                "download-64k",
                "download-1m",
                "download-near-4m",
            ),
            cases.map(BenchmarkCase::name),
        )
        assertEquals(
            BenchmarkParameters(5, 200, 1, 1_024 * 1_024, 64),
            cases.single { it.name == "upload-1m" }.parameters,
        )
        assertEquals(
            BenchmarkParameters(20, 2_000, 1, 64, 64 * 1_024),
            cases.single { it.name == "download-64k" }.parameters,
        )
        assertEquals(
            BenchmarkParameters(2, 50, 1, 4 * 1_024 * 1_024 - 1_024, 64),
            cases.single { it.name == "upload-near-4m" }.parameters,
        )
    }

    @Test
    fun definesConcurrencySweepCases() {
        val cases = requireNotNull(registry.find("unary-concurrency-sweep")).cases

        assertEquals(16, cases.size)
        assertEquals(
            BenchmarkParameters(100, 10_000, 1, 0, 0),
            cases.single { it.name == "empty-c1" }.parameters,
        )
        assertEquals(
            BenchmarkParameters(1_280, 128_000, 128, 1_024, 1_024),
            cases.single { it.name == "1k-c128" }.parameters,
        )
    }

    private object TestBenchmarkBackend : BenchmarkBackend {
        override val implementationName: String = "test"

        override fun prepareUnaryCall(client: GrpcClient): suspend (SimpleRequest) -> SimpleResponse =
            error("Benchmark execution is not expected in definition tests")

        override fun preparePingPongCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
            error("Benchmark execution is not expected in definition tests")

        override fun prepareClientStreamingCall(client: GrpcClient): suspend (Flow<SimpleRequest>) -> SimpleResponse =
            error("Benchmark execution is not expected in definition tests")

        override fun prepareServerStreamingCall(client: GrpcClient): (SimpleRequest) -> Flow<SimpleResponse> =
            error("Benchmark execution is not expected in definition tests")

        override fun prepareFullDuplexCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
            error("Benchmark execution is not expected in definition tests")
    }
}
