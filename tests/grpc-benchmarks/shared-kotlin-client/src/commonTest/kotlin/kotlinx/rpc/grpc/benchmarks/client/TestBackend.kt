/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.client.GrpcClient

/** A backend for tests that don't execute calls. */
internal object TestBenchmarkBackend : BenchmarkBackend {
    override val implementationName: String = "test"

    override val implementationVersion: String? = "test-1.0"

    override fun prepareUnaryCall(client: GrpcClient): suspend (SimpleRequest) -> SimpleResponse =
        error("Benchmark execution is not expected in this test")

    override fun preparePingPongCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
        error("Benchmark execution is not expected in this test")

    override fun prepareClientStreamingCall(client: GrpcClient): suspend (Flow<SimpleRequest>) -> SimpleResponse =
        error("Benchmark execution is not expected in this test")

    override fun prepareServerStreamingCall(client: GrpcClient): (SimpleRequest) -> Flow<SimpleResponse> =
        error("Benchmark execution is not expected in this test")

    override fun prepareFullDuplexCall(client: GrpcClient): (Flow<SimpleRequest>) -> Flow<SimpleResponse> =
        error("Benchmark execution is not expected in this test")
}
