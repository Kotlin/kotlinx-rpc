/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.invoke
import kotlinx.io.bytestring.ByteString

internal fun unaryBenchmarks(backend: BenchmarkBackend) = listOf(
    unaryBenchmark(
        backend = backend,
        name = "unary-latency",
        description = "Warm sequential unary call latency",
        cases = listOf(
            benchmarkCase(
                warmupOperations = 200,
                operations = 10_000,
                concurrency = 1,
                requestBytes = 0,
                responseBytes = 0,
            ),
        ),
    ),
    unaryBenchmark(
        backend = backend,
        name = "unary-throughput",
        description = "Warm concurrent unary call throughput",
        cases = listOf(
            benchmarkCase(
                warmupOperations = 200,
                operations = 100_000,
                concurrency = 16,
                requestBytes = 1_024,
                responseBytes = 1_024,
            ),
        ),
    ),
    unaryBenchmark(
        backend = backend,
        name = "unary-payload-sweep",
        description = "Unary request and response payload size sweep",
        cases = payloadSweepCases(),
    ),
    unaryBenchmark(
        backend = backend,
        name = "unary-concurrency-sweep",
        description = "Unary concurrency sweep with empty and 1 KiB payloads",
        cases = concurrencySweepCases(),
    ),
)

private fun unaryBenchmark(
    backend: BenchmarkBackend,
    name: String,
    description: String,
    cases: List<BenchmarkCase>,
): Benchmark {
    return CallBenchmark(name, description, cases) { client, parameters ->
        val unaryCall = backend.prepareUnaryCall(client)
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)

        MeasuredCall(
            applicationRequestBytes = parameters.requestBytes.toLong(),
            applicationResponseBytes = parameters.responseBytes.toLong(),
            execute = {
                val response = unaryCall(request)
                check(response.payload.body.size == parameters.responseBytes) {
                    "Expected ${parameters.responseBytes} response bytes, got ${response.payload.body.size}"
                }
            },
        )
    }
}

private data class PayloadSweepSize(
    val name: String,
    val bytes: Int,
    val warmupOperations: Int,
    val operations: Int,
)

private val payloadSweepSizes = listOf(
    PayloadSweepSize("64b", 64, warmupOperations = 200, operations = 10_000),
    PayloadSweepSize("1k", 1_024, warmupOperations = 200, operations = 10_000),
    PayloadSweepSize("64k", 64 * 1_024, warmupOperations = 50, operations = 3_000),
    PayloadSweepSize("1m", 1_024 * 1_024, warmupOperations = 10, operations = 600),
    PayloadSweepSize(
        "near-4m",
        4 * 1_024 * 1_024 - 1_024,
        warmupOperations = 5,
        operations = 150,
    ),
)

private fun payloadSweepCases(): List<BenchmarkCase> = buildList {
    for (size in payloadSweepSizes) {
        add(payloadCase("symmetric-${size.name}", size, size.bytes, size.bytes))
    }
    for (size in payloadSweepSizes.drop(1)) {
        add(payloadCase("upload-${size.name}", size, size.bytes, PAYLOAD_SWEEP_ANCHOR_BYTES))
    }
    for (size in payloadSweepSizes.drop(1)) {
        add(payloadCase("download-${size.name}", size, PAYLOAD_SWEEP_ANCHOR_BYTES, size.bytes))
    }
}

private fun payloadCase(
    name: String,
    size: PayloadSweepSize,
    requestBytes: Int,
    responseBytes: Int,
): BenchmarkCase = benchmarkCase(
    name = name,
    warmupOperations = size.warmupOperations,
    operations = size.operations,
    concurrency = 1,
    requestBytes = requestBytes,
    responseBytes = responseBytes,
)

private fun concurrencySweepCases(): List<BenchmarkCase> = buildList {
    for ((payloadName, payloadBytes) in listOf("empty" to 0, "1k" to 1_024)) {
        for (concurrency in listOf(1, 2, 4, 8, 16, 32, 64, 128)) {
            add(
                benchmarkCase(
                    name = "$payloadName-c$concurrency",
                    warmupOperations = maxOf(200, concurrency * 20),
                    operations = maxOf(20_000, concurrency * 2_000),
                    concurrency = concurrency,
                    requestBytes = payloadBytes,
                    responseBytes = payloadBytes,
                ),
            )
        }
    }
}

internal fun benchmarkCase(
    name: String = "default",
    warmupOperations: Int,
    operations: Int,
    concurrency: Int,
    requestBytes: Int,
    responseBytes: Int,
): BenchmarkCase = BenchmarkCase(
    name = name,
    parameters = BenchmarkParameters(
        warmupOperations = warmupOperations,
        operations = operations,
        concurrency = concurrency,
        requestBytes = requestBytes,
        responseBytes = responseBytes,
    ),
)

private const val PAYLOAD_SWEEP_ANCHOR_BYTES = 64

internal fun benchmarkRequest(requestBytes: Int, responseBytes: Int): SimpleRequest {
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
