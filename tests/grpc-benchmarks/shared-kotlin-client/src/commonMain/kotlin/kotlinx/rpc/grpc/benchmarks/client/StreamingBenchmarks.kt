/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import io.grpc.testing.integration.SimpleRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlin.time.TimeSource

internal fun streamingBenchmarks(backend: BenchmarkBackend): List<Benchmark> = listOf(
    serverStreamingBenchmark(
        backend = backend,
        name = "server-streaming-throughput",
        description = "Server-streaming download throughput for small and large messages",
        cases = serverThroughputCases(),
    ),
    clientStreamingBenchmark(
        backend = backend,
        name = "client-streaming-throughput",
        description = "Client-streaming upload throughput for small and large messages",
        cases = clientThroughputCases(),
    ),
    pingPongBenchmark(backend),
    fullDuplexBenchmark(backend),
    clientStreamingBenchmark(
        backend = backend,
        name = "stream-message-overhead",
        description = "Upload the same 64 MiB using different message sizes",
        cases = messageOverheadCases(),
        fixedMeasuredRequestBytes = MESSAGE_OVERHEAD_TOTAL_BYTES,
    ),
    serverStreamingBenchmark(
        backend = backend,
        name = "stream-concurrency-sweep",
        description = "Server-streaming concurrency sweep on one channel",
        cases = streamConcurrencyCases(),
    ),
)

private fun serverStreamingBenchmark(
    backend: BenchmarkBackend,
    name: String,
    description: String,
    cases: List<BenchmarkCase>,
): Benchmark = StreamingBenchmark(name, description, cases, backend.implementationName) { client ->
    val call = backend.prepareServerStreamingCall(client)
    val executeStream: suspend (Int, BenchmarkParameters) -> StreamMeasurement = { messages, parameters ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val samples = LongArray(messages)
        val streamStarted = TimeSource.Monotonic.markNow()
        var previousMessage = streamStarted
        var received = 0

        call(request).take(messages).collect { response ->
            val latency = previousMessage.elapsedNow().inWholeNanoseconds
            previousMessage = TimeSource.Monotonic.markNow()
            checkResponseSize(response.payload.body.size, parameters.responseBytes)
            samples[received++] = latency
        }
        check(received == messages) { "Expected $messages responses, got $received" }

        StreamMeasurement(
            requestMessages = 1,
            responseMessages = messages.toLong(),
            applicationBytes = parameters.requestBytes.toLong() + parameters.responseBytes.toLong() * messages,
            latencySamples = samples,
            timeToFirstResponseNanoseconds = samples.first(),
        )
    }
    executeStream
}

private fun clientStreamingBenchmark(
    backend: BenchmarkBackend,
    name: String,
    description: String,
    cases: List<BenchmarkCase>,
    fixedMeasuredRequestBytes: Long? = null,
): Benchmark = StreamingBenchmark(name, description, cases, backend.implementationName) { client ->
    val call = backend.prepareClientStreamingCall(client)
    val executeStream: suspend (Int, BenchmarkParameters) -> StreamMeasurement = { messages, parameters ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val measuredFixedTotal = fixedMeasuredRequestBytes?.takeIf { totalBytes ->
            parameters.requestBytes > 0 &&
            messages.toLong() == (totalBytes + parameters.requestBytes - 1) / parameters.requestBytes
        }
        val finalRequest = measuredFixedTotal?.let { totalBytes ->
            val finalRequestBytes = totalBytes - parameters.requestBytes.toLong() * (messages - 1)
            require(finalRequestBytes in 1..parameters.requestBytes.toLong()) {
                "The fixed request total must fit in $messages messages"
            }
            benchmarkRequest(finalRequestBytes.toInt(), parameters.responseBytes)
        }
        val samples = LongArray(messages)
        val streamStarted = TimeSource.Monotonic.markNow()
        var finalMessageSent = streamStarted
        val response = call(
            flow {
                repeat(messages) { index ->
                    val sendStarted = TimeSource.Monotonic.markNow()
                    emit(if (index == messages - 1) finalRequest ?: request else request)
                    samples[index] = sendStarted.elapsedNow().inWholeNanoseconds
                    finalMessageSent = TimeSource.Monotonic.markNow()
                }
            },
        )
        val finalResponseLatency = finalMessageSent.elapsedNow().inWholeNanoseconds
        checkResponseSize(response.payload.body.size, parameters.responseBytes)

        StreamMeasurement(
            requestMessages = messages.toLong(),
            responseMessages = 1,
            applicationBytes = (measuredFixedTotal ?: parameters.requestBytes.toLong() * messages) +
                parameters.responseBytes,
            latencySamples = samples,
            timeToFirstResponseNanoseconds = streamStarted.elapsedNow().inWholeNanoseconds,
            finalResponseLatencyNanoseconds = finalResponseLatency,
        )
    }
    executeStream
}

private fun pingPongBenchmark(backend: BenchmarkBackend): Benchmark = StreamingBenchmark(
    name = "bidi-ping-pong",
    description = "Strict send-one/receive-one bidirectional streaming latency",
    cases = listOf(
        benchmarkCase("empty", 100, 10_000, 1, 0, 0),
        benchmarkCase("1k", 100, 10_000, 1, 1_024, 1_024),
    ),
    implementationName = backend.implementationName,
) { client ->
    val call = backend.preparePingPongCall(client)
    val executeStream: suspend (Int, BenchmarkParameters) -> StreamMeasurement = { messages, parameters ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val requests = Channel<SimpleRequest>(capacity = 1)
        val samples = LongArray(messages)
        var sentAt = TimeSource.Monotonic.markNow()
        requests.trySend(request).getOrThrow()
        var received = 0

        try {
            call(requests.receiveAsFlow()).take(messages).collect { response ->
                checkResponseSize(response.payload.body.size, parameters.responseBytes)
                samples[received] = sentAt.elapsedNow().inWholeNanoseconds
                received++
                if (received < messages) {
                    sentAt = TimeSource.Monotonic.markNow()
                    requests.send(request)
                }
            }
        } finally {
            requests.close()
        }
        check(received == messages) { "Expected $messages responses, got $received" }

        StreamMeasurement(
            requestMessages = messages.toLong(),
            responseMessages = messages.toLong(),
            applicationBytes = (parameters.requestBytes.toLong() + parameters.responseBytes) * messages,
            latencySamples = samples,
            timeToFirstResponseNanoseconds = samples.first(),
        )
    }
    executeStream
}

private fun fullDuplexBenchmark(backend: BenchmarkBackend): Benchmark = StreamingBenchmark(
    name = "bidi-full-duplex",
    description = "Independent concurrent upload and download over one stream",
    cases = listOf(
        benchmarkCase("balanced-1k", 100, 10_000, 1, 1_024, 1_024),
        benchmarkCase("balanced-64k", 10, 1_000, 1, 64 * 1_024, 64 * 1_024),
        benchmarkCase("upload-heavy", 10, 1_000, 1, 64 * 1_024, 1_024),
        benchmarkCase("download-heavy", 10, 1_000, 1, 1_024, 64 * 1_024),
    ),
    implementationName = backend.implementationName,
) { client ->
    val call = backend.prepareFullDuplexCall(client)
    val executeStream: suspend (Int, BenchmarkParameters) -> StreamMeasurement = { messages, parameters ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val samples = mutableListOf<Long>()
        val streamStarted = TimeSource.Monotonic.markNow()
        var previousMessage = streamStarted
        val requests: Flow<SimpleRequest> = flow {
            repeat(messages) { emit(request) }
        }

        call(requests).collect { response ->
            val latency = previousMessage.elapsedNow().inWholeNanoseconds
            previousMessage = TimeSource.Monotonic.markNow()
            checkResponseSize(response.payload.body.size, parameters.responseBytes)
            samples += latency
        }
        check(samples.size == messages) { "Expected $messages responses, got ${samples.size}" }

        StreamMeasurement(
            requestMessages = messages.toLong(),
            responseMessages = messages.toLong(),
            applicationBytes = (parameters.requestBytes.toLong() + parameters.responseBytes) * messages,
            latencySamples = samples.toLongArray(),
            timeToFirstResponseNanoseconds = samples.first(),
        )
    }
    executeStream
}

private fun serverThroughputCases(): List<BenchmarkCase> = listOf(
    benchmarkCase("1k", 100, 10_000, 1, STREAM_CONTROL_PAYLOAD_BYTES, 1_024),
    benchmarkCase("64k", 10, 1_000, 1, STREAM_CONTROL_PAYLOAD_BYTES, 64 * 1_024),
    benchmarkCase("1m", 2, 64, 1, STREAM_CONTROL_PAYLOAD_BYTES, 1_024 * 1_024),
)

private fun clientThroughputCases(): List<BenchmarkCase> = listOf(
    benchmarkCase("1k", 100, 10_000, 1, 1_024, STREAM_CONTROL_PAYLOAD_BYTES),
    benchmarkCase("64k", 10, 1_000, 1, 64 * 1_024, STREAM_CONTROL_PAYLOAD_BYTES),
    benchmarkCase("1m", 2, 64, 1, 1_024 * 1_024, STREAM_CONTROL_PAYLOAD_BYTES),
)

private fun messageOverheadCases(): List<BenchmarkCase> = listOf(
    benchmarkCase("1k", 128, 64 * 1_024, 1, 1_024, 0),
    benchmarkCase("64k", 16, 1_024, 1, 64 * 1_024, 0),
    benchmarkCase("1m", 2, 64, 1, 1_024 * 1_024, 0),
    benchmarkCase("near-4m", 1, 17, 1, 4 * 1_024 * 1_024 - 1_024, 0),
)

// StreamingFromServer is unbounded, so completing each measurement resets its stream. SwiftNIO
// retains a bounded number of recently reset HTTP/2 streams and can close the connection when late
// frames arrive for an evicted stream. Stop at 16 so the warmup does not exhaust that allowance.
private fun streamConcurrencyCases(): List<BenchmarkCase> = listOf(1, 2, 4, 8, 16).map { concurrency ->
    benchmarkCase(
        name = "c$concurrency",
        warmupCalls = maxOf(128, concurrency * 4),
        calls = maxOf(8_192, concurrency * 128),
        concurrency = concurrency,
        requestBytes = 64,
        responseBytes = 1_024,
    )
}

private fun checkResponseSize(actual: Int, expected: Int) {
    check(actual == expected) { "Expected $expected response bytes, got $actual" }
}

private const val MESSAGE_OVERHEAD_TOTAL_BYTES = 64L * 1_024 * 1_024
private const val STREAM_CONTROL_PAYLOAD_BYTES = 64
