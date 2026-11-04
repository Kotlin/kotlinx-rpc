/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import io.grpc.testing.integration.SimpleRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlin.time.Duration.Companion.milliseconds
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
): Benchmark = StreamingBenchmark(
    name = name,
    description = description,
    cases = cases,
    latencyMetric = LatencyMetric.MESSAGE_INTERARRIVAL,
) { client ->
    val call = backend.prepareServerStreamingCall(client)
    val executeStream: suspend (Int, BenchmarkParameters, Int) -> StreamMeasurement = { messages, parameters, injectedDelayMs ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val samples = ArrayList<Long>(messages - 1)
        val streamStarted = TimeSource.Monotonic.markNow()
        var previous: TimeSource.Monotonic.ValueTimeMark? = null
        var received = 0
        var timeToFirstResponseNanos: Long? = null

        call(request).take(messages).collect { response ->
            if (received == 0) {
                timeToFirstResponseNanos = streamStarted.elapsedNow().inWholeNanoseconds
            }
            previous?.let { samples += it.elapsedNow().inWholeNanoseconds }
            previous = TimeSource.Monotonic.markNow()
            checkResponseSize(response.payload.body.size, parameters.responseBytes)
            received++
            // The delay paces the consumer inside the next inter-arrival window.
            if (injectedDelayMs > 0 && received < messages) {
                delay(injectedDelayMs.milliseconds)
            }
        }
        check(received == messages) { "Expected $messages responses, got $received" }

        StreamMeasurement(
            requestMessages = 1,
            responseMessages = messages.toLong(),
            applicationRequestBytes = parameters.requestBytes.toLong(),
            applicationResponseBytes = parameters.responseBytes.toLong() * messages,
            latencySamples = samples.toLongArray(),
            timeToFirstResponseNanos = timeToFirstResponseNanos,
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
): Benchmark = StreamingBenchmark(
    name = name,
    description = description,
    cases = cases,
    latencyMetric = LatencyMetric.REQUEST_WRITE_SUSPENSION,
) { client ->
    val call = backend.prepareClientStreamingCall(client)
    val executeStream: suspend (Int, BenchmarkParameters, Int) -> StreamMeasurement = { messages, parameters, injectedDelayMs ->
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
        val samples = ArrayList<Long>(messages)
        val streamStarted = TimeSource.Monotonic.markNow()
        var finalMessageSent: TimeSource.Monotonic.ValueTimeMark? = null
        val response = call(
            flow {
                repeat(messages) { index ->
                    val sendStarted = TimeSource.Monotonic.markNow()
                    if (injectedDelayMs > 0) {
                        delay(injectedDelayMs.milliseconds)
                    }
                    emit(if (index == messages - 1) finalRequest ?: request else request)
                    samples += sendStarted.elapsedNow().inWholeNanoseconds
                    if (index == messages - 1) {
                        finalMessageSent = TimeSource.Monotonic.markNow()
                    }
                }
            },
        )
        val finalResponseLatency = finalMessageSent!!.elapsedNow().inWholeNanoseconds
        checkResponseSize(response.payload.body.size, parameters.responseBytes)

        StreamMeasurement(
            requestMessages = messages.toLong(),
            responseMessages = 1,
            applicationRequestBytes = measuredFixedTotal ?: parameters.requestBytes.toLong() * messages,
            applicationResponseBytes = parameters.responseBytes.toLong(),
            latencySamples = samples.toLongArray(),
            streamDurationNanos = streamStarted.elapsedNow().inWholeNanoseconds,
            finalResponseLatencyNanos = finalResponseLatency,
        )
    }
    executeStream
}

private fun pingPongBenchmark(backend: BenchmarkBackend): Benchmark = StreamingBenchmark(
    name = "bidi-ping-pong",
    description = "Strict send-one/receive-one bidirectional streaming latency",
    cases = listOf(
        benchmarkCase(name = "empty", warmupOperations = 200, operations = 10_000, concurrency = 1, requestBytes = 0, responseBytes = 0),
        benchmarkCase(name = "1k", warmupOperations = 200, operations = 10_000, concurrency = 1, requestBytes = 1_024, responseBytes = 1_024),
    ),
    latencyMetric = LatencyMetric.PING_PONG_ROUND_TRIP,
) { client ->
    val call = backend.preparePingPongCall(client)
    val executeStream: suspend (Int, BenchmarkParameters, Int) -> StreamMeasurement = { messages, parameters, injectedDelayMs ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val requests = Channel<SimpleRequest>(capacity = 1)
        val samples = ArrayList<Long>(messages)
        var sentAt = TimeSource.Monotonic.markNow()
        requests.trySend(request).getOrThrow()
        var received = 0

        try {
            call(requests.receiveAsFlow()).take(messages).collect { response ->
                checkResponseSize(response.payload.body.size, parameters.responseBytes)
                samples += sentAt.elapsedNow().inWholeNanoseconds
                received++
                if (received < messages) {
                    sentAt = TimeSource.Monotonic.markNow()
                    // The delay runs inside the next round-trip window.
                    if (injectedDelayMs > 0) {
                        delay(injectedDelayMs.milliseconds)
                    }
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
            applicationRequestBytes = parameters.requestBytes.toLong() * messages,
            applicationResponseBytes = parameters.responseBytes.toLong() * messages,
            latencySamples = samples.toLongArray(),
            timeToFirstResponseNanos = samples.first(),
        )
    }
    executeStream
}

private fun fullDuplexBenchmark(backend: BenchmarkBackend): Benchmark = StreamingBenchmark(
    name = "bidi-full-duplex",
    description = "Independent concurrent upload and download over one stream",
    cases = listOf(
        benchmarkCase(name = "balanced-1k", warmupOperations = 200, operations = 10_000, concurrency = 1, requestBytes = 1_024, responseBytes = 1_024),
        benchmarkCase(name = "balanced-64k", warmupOperations = 20, operations = 2_000, concurrency = 1, requestBytes = 64 * 1_024, responseBytes = 64 * 1_024),
        benchmarkCase(name = "upload-heavy", warmupOperations = 20, operations = 2_000, concurrency = 1, requestBytes = 64 * 1_024, responseBytes = 1_024),
        benchmarkCase(name = "download-heavy", warmupOperations = 20, operations = 2_000, concurrency = 1, requestBytes = 1_024, responseBytes = 64 * 1_024),
    ),
    latencyMetric = LatencyMetric.MESSAGE_INTERARRIVAL,
) { client ->
    val call = backend.prepareFullDuplexCall(client)
    val executeStream: suspend (Int, BenchmarkParameters, Int) -> StreamMeasurement = { messages, parameters, injectedDelayMs ->
        val request = benchmarkRequest(parameters.requestBytes, parameters.responseBytes)
        val samples = ArrayList<Long>(messages - 1)
        val streamStarted = TimeSource.Monotonic.markNow()
        var previous: TimeSource.Monotonic.ValueTimeMark? = null
        var received = 0
        var timeToFirstResponseNanos: Long? = null
        val requests: Flow<SimpleRequest> = flow {
            repeat(messages) {
                // The delay paces the producer, so it widens the response inter-arrival times.
                if (injectedDelayMs > 0) {
                    delay(injectedDelayMs.milliseconds)
                }
                emit(request)
            }
        }

        call(requests).collect { response ->
            if (received == 0) {
                timeToFirstResponseNanos = streamStarted.elapsedNow().inWholeNanoseconds
            }
            previous?.let { samples += it.elapsedNow().inWholeNanoseconds }
            previous = TimeSource.Monotonic.markNow()
            checkResponseSize(response.payload.body.size, parameters.responseBytes)
            received++
        }
        check(received == messages) { "Expected $messages responses, got $received" }

        StreamMeasurement(
            requestMessages = messages.toLong(),
            responseMessages = messages.toLong(),
            applicationRequestBytes = parameters.requestBytes.toLong() * messages,
            applicationResponseBytes = parameters.responseBytes.toLong() * messages,
            latencySamples = samples.toLongArray(),
            timeToFirstResponseNanos = timeToFirstResponseNanos,
        )
    }
    executeStream
}

private fun serverThroughputCases(): List<BenchmarkCase> = listOf(
    benchmarkCase(name = "1k", warmupOperations = 200, operations = 40_000, concurrency = 1, requestBytes = STREAM_CONTROL_PAYLOAD_BYTES, responseBytes = 1_024),
    benchmarkCase(name = "64k", warmupOperations = 20, operations = 2_000, concurrency = 1, requestBytes = STREAM_CONTROL_PAYLOAD_BYTES, responseBytes = 64 * 1_024),
    benchmarkCase(name = "1m", warmupOperations = 5, operations = 256, concurrency = 1, requestBytes = STREAM_CONTROL_PAYLOAD_BYTES, responseBytes = 1_024 * 1_024),
)

private fun clientThroughputCases(): List<BenchmarkCase> = listOf(
    benchmarkCase(name = "1k", warmupOperations = 200, operations = 40_000, concurrency = 1, requestBytes = 1_024, responseBytes = STREAM_CONTROL_PAYLOAD_BYTES),
    benchmarkCase(name = "64k", warmupOperations = 20, operations = 2_000, concurrency = 1, requestBytes = 64 * 1_024, responseBytes = STREAM_CONTROL_PAYLOAD_BYTES),
    benchmarkCase(name = "1m", warmupOperations = 5, operations = 256, concurrency = 1, requestBytes = 1_024 * 1_024, responseBytes = STREAM_CONTROL_PAYLOAD_BYTES),
)

private fun messageOverheadCases(): List<BenchmarkCase> = listOf(
    benchmarkCase(name = "1k", warmupOperations = 128, operations = 64 * 1_024, concurrency = 1, requestBytes = 1_024, responseBytes = 0),
    benchmarkCase(name = "64k", warmupOperations = 16, operations = 1_024, concurrency = 1, requestBytes = 64 * 1_024, responseBytes = 0),
    benchmarkCase(name = "1m", warmupOperations = 2, operations = 64, concurrency = 1, requestBytes = 1_024 * 1_024, responseBytes = 0),
    benchmarkCase(name = "near-4m", warmupOperations = 1, operations = 17, concurrency = 1, requestBytes = 4 * 1_024 * 1_024 - 1_024, responseBytes = 0),
)

// StreamingFromServer is unbounded, so completing each stream cancels its RPC. SwiftNIO retains a
// bounded number of recently reset HTTP/2 streams and can close the connection when late frames
// arrive for an evicted stream. Stop at 16 so the warmup does not exhaust that allowance.
private fun streamConcurrencyCases(): List<BenchmarkCase> = listOf(1, 2, 4, 8, 16).map { concurrency ->
    benchmarkCase(
        name = "c$concurrency",
        warmupOperations = maxOf(200, concurrency * 8),
        operations = maxOf(16_384, concurrency * 512),
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
