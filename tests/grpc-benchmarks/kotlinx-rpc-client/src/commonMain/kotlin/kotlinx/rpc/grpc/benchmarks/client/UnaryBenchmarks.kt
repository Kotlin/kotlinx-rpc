/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import grpc.testing.BenchmarkService
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.invoke
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.withService

internal fun unaryBenchmarks() = listOf(
    unaryBenchmark(
        name = "unary-latency",
        description = "Warm sequential unary call latency",
        defaults = BenchmarkParameters(
            warmupCalls = 100,
            calls = 1_000,
            concurrency = 1,
            requestBytes = 0,
            responseBytes = 0,
        ),
    ),
    unaryBenchmark(
        name = "unary-throughput",
        description = "Warm concurrent unary call throughput",
        defaults = BenchmarkParameters(
            warmupCalls = 100,
            calls = 10_000,
            concurrency = 16,
            requestBytes = 1_024,
            responseBytes = 1_024,
        ),
    ),
)

private fun unaryBenchmark(
    name: String,
    description: String,
    defaults: BenchmarkParameters,
): Benchmark {
    return CallBenchmark(name, description, defaults) { client, parameters ->
        val service = client.withService<BenchmarkService>()
        val request = request(parameters.requestBytes, parameters.responseBytes)

        MeasuredCall(
            applicationBytes = parameters.requestBytes.toLong() + parameters.responseBytes,
            execute = {
                val response = service.unaryCall(request)
                check(response.payload.body.size == parameters.responseBytes) {
                    "Expected ${parameters.responseBytes} response bytes, got ${response.payload.body.size}"
                }
            },
        )
    }
}

private fun request(requestBytes: Int, responseBytes: Int): SimpleRequest {
    val body = ByteString(*ByteArray(requestBytes) { index -> (index % 251).toByte() })
    return SimpleRequest {
        responseType = PayloadType.COMPRESSABLE
        responseSize = responseBytes
        payload = Payload {
            type = PayloadType.COMPRESSABLE
            this.body = body
        }
    }
}
