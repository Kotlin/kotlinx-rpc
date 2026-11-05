/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCProtobuf

func streamingBenchmarks() -> [any Benchmark] {
    [
        serverStreamingBenchmark(
            name: "server-streaming-throughput",
            description: "Server-streaming download throughput for small and large messages",
            cases: serverThroughputCases()
        ),
        clientStreamingBenchmark(
            name: "client-streaming-throughput",
            description: "Client-streaming upload throughput for small and large messages",
            cases: clientThroughputCases()
        ),
        pingPongBenchmark(),
        fullDuplexBenchmark(),
        clientStreamingBenchmark(
            name: "stream-message-overhead",
            description: "Upload the same 64 MiB using different message sizes",
            cases: messageOverheadCases(),
            fixedMeasuredRequestBytes: messageOverheadTotalBytes
        ),
        serverStreamingBenchmark(
            name: "stream-concurrency-sweep",
            description: "Server-streaming concurrency sweep on one channel",
            cases: streamConcurrencyCases()
        ),
    ]
}

private func serverStreamingBenchmark(
    name: String,
    description: String,
    cases: [BenchmarkCase]
) -> any Benchmark {
    StreamingBenchmark(
        name: name,
        description: description,
        cases: cases,
        latencyMetric: .messageInterarrival
    ) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters, injectedDelayMs in
            let request = streamingRequest(
                requestBytes: parameters.requestBytes,
                responseBytes: parameters.responseBytes
            )
            let streamStarted = monotonicNanoseconds()

            return try await service.streamingFromServer(request) { response in
                var previous: UInt64? = nil
                var firstMessageNanos: UInt64? = nil
                var samples: [Int64] = []
                samples.reserveCapacity(messages - 1)
                var received = 0

                for try await message in response.messages {
                    let now = monotonicNanoseconds()
                    if let previous {
                        samples.append(Int64(now - previous))
                    }
                    if firstMessageNanos == nil {
                        firstMessageNanos = now
                    }
                    previous = now
                    try validateResponse(message, expectedBytes: parameters.responseBytes)
                    received += 1
                    // The delay paces the consumer inside the next inter-arrival window.
                    if injectedDelayMs > 0 && received < messages {
                        try await Task.sleep(nanoseconds: UInt64(injectedDelayMs) * 1_000_000)
                    }
                    if received == messages { break }
                }
                guard received == messages else {
                    throw UnexpectedStreamMessageCount(expected: messages, actual: received)
                }
                return StreamMeasurement(
                    requestMessages: 1,
                    responseMessages: Int64(messages),
                    applicationRequestBytes: Int64(parameters.requestBytes),
                    applicationResponseBytes: Int64(parameters.responseBytes) * Int64(messages),
                    latencySamples: samples,
                    timeToFirstResponseNanoseconds: Int64(firstMessageNanos! - streamStarted),
                    streamDurationNanoseconds: nil,
                    finalResponseLatencyNanoseconds: nil
                )
            }
        }
    }
}

private func clientStreamingBenchmark(
    name: String,
    description: String,
    cases: [BenchmarkCase],
    fixedMeasuredRequestBytes: Int64? = nil
) -> any Benchmark {
    StreamingBenchmark(
        name: name,
        description: description,
        cases: cases,
        latencyMetric: .requestWriteSuspension
    ) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters, injectedDelayMs in
            let request = streamingRequest(
                requestBytes: parameters.requestBytes,
                responseBytes: parameters.responseBytes
            )
            let measuredFixedTotal: Int64? = fixedMeasuredRequestBytes.flatMap { totalBytes -> Int64? in
                guard parameters.requestBytes > 0 else { return nil }
                let expectedMessages = (totalBytes + Int64(parameters.requestBytes) - 1) /
                    Int64(parameters.requestBytes)
                return Int64(messages) == expectedMessages ? totalBytes : nil
            }
            let finalRequest = try measuredFixedTotal.map { totalBytes in
                let finalRequestBytes = totalBytes - Int64(parameters.requestBytes) * Int64(messages - 1)
                guard (1...Int64(parameters.requestBytes)).contains(finalRequestBytes) else {
                    throw InvalidFixedStreamSize()
                }
                return streamingRequest(
                    requestBytes: Int(finalRequestBytes),
                    responseBytes: parameters.responseBytes
                )
            }
            let recorder = SendLatencyRecorder(capacity: messages)
            let streamStarted = monotonicNanoseconds()
            let response = try await service.streamingFromClient(requestProducer: { writer in
                for index in 0..<messages {
                    let sendStarted = monotonicNanoseconds()
                    if injectedDelayMs > 0 {
                        try await Task.sleep(nanoseconds: UInt64(injectedDelayMs) * 1_000_000)
                    }
                    try await writer.write(index == messages - 1 ? finalRequest ?? request : request)
                    recorder.record(monotonicNanoseconds() - sendStarted)
                }
            })
            let responseReceived = monotonicNanoseconds()
            try validateResponse(response, expectedBytes: parameters.responseBytes)
            let snapshot = recorder.snapshot()

            return StreamMeasurement(
                requestMessages: Int64(messages),
                responseMessages: 1,
                applicationRequestBytes: measuredFixedTotal ?? Int64(parameters.requestBytes) * Int64(messages),
                applicationResponseBytes: Int64(parameters.responseBytes),
                latencySamples: snapshot.samples,
                timeToFirstResponseNanoseconds: nil,
                streamDurationNanoseconds: Int64(responseReceived - streamStarted),
                finalResponseLatencyNanoseconds: Int64(responseReceived - snapshot.finalMessageSent)
            )
        }
    }
}

private func pingPongBenchmark() -> any Benchmark {
    StreamingBenchmark(
        name: "bidi-ping-pong",
        description: "Strict send-one/receive-one bidirectional streaming latency",
        cases: [
            streamingBenchmarkCase(name: "empty", warmupOperations: 200, operations: 10_000, concurrency: 1),
            streamingBenchmarkCase(
                name: "1k",
                warmupOperations: 200,
                operations: 10_000,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 1_024
            )
        ],
        latencyMetric: .pingPongRoundTrip
    ) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters, injectedDelayMs in
            let request = streamingRequest(
                requestBytes: parameters.requestBytes,
                responseBytes: parameters.responseBytes
            )
            let nextRequest = AsyncStream<Void>.makeStream()
            let firstRequestStarted = monotonicNanoseconds()

            return try await service.streamingCall(
                requestProducer: { writer in
                    try await writer.write(request)
                    var iterator = nextRequest.stream.makeAsyncIterator()
                    for _ in 1..<messages {
                        _ = await iterator.next()
                        // The delay runs inside the next round-trip window.
                        if injectedDelayMs > 0 {
                            try await Task.sleep(nanoseconds: UInt64(injectedDelayMs) * 1_000_000)
                        }
                        try await writer.write(request)
                    }
                },
                onResponse: { response in
                    defer { nextRequest.continuation.finish() }
                    var sentAt = firstRequestStarted
                    var samples: [Int64] = []
                    samples.reserveCapacity(messages)

                    for try await message in response.messages {
                        let now = monotonicNanoseconds()
                        samples.append(Int64(now - sentAt))
                        try validateResponse(message, expectedBytes: parameters.responseBytes)
                        if samples.count == messages { break }
                        sentAt = monotonicNanoseconds()
                        nextRequest.continuation.yield()
                    }
                    guard samples.count == messages else {
                        throw UnexpectedStreamMessageCount(expected: messages, actual: samples.count)
                    }
                    return StreamMeasurement(
                        requestMessages: Int64(messages),
                        responseMessages: Int64(messages),
                        applicationRequestBytes: Int64(parameters.requestBytes) * Int64(messages),
                        applicationResponseBytes: Int64(parameters.responseBytes) * Int64(messages),
                        latencySamples: samples,
                        timeToFirstResponseNanoseconds: samples.first,
                        streamDurationNanoseconds: nil,
                        finalResponseLatencyNanoseconds: nil
                    )
                }
            )
        }
    }
}

private func fullDuplexBenchmark() -> any Benchmark {
    StreamingBenchmark(
        name: "bidi-full-duplex",
        description: "Independent concurrent upload and download over one stream",
        cases: [
            streamingBenchmarkCase(
                name: "balanced-1k",
                warmupOperations: 200,
                operations: 10_000,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 1_024
            ),
            streamingBenchmarkCase(
                name: "balanced-64k",
                warmupOperations: 20,
                operations: 2_000,
                concurrency: 1,
                requestBytes: 64 * 1_024,
                responseBytes: 64 * 1_024
            ),
            streamingBenchmarkCase(
                name: "upload-heavy",
                warmupOperations: 20,
                operations: 2_000,
                concurrency: 1,
                requestBytes: 64 * 1_024,
                responseBytes: 1_024
            ),
            streamingBenchmarkCase(
                name: "download-heavy",
                warmupOperations: 20,
                operations: 2_000,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 64 * 1_024
            )
        ],
        latencyMetric: .messageInterarrival
    ) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters, injectedDelayMs in
            let request = streamingRequest(
                requestBytes: parameters.requestBytes,
                responseBytes: parameters.responseBytes
            )
            let streamStarted = monotonicNanoseconds()

            return try await service.streamingCall(
                requestProducer: { writer in
                    for _ in 0..<messages {
                        // The delay paces the producer, so it widens the response inter-arrival times.
                        if injectedDelayMs > 0 {
                            try await Task.sleep(nanoseconds: UInt64(injectedDelayMs) * 1_000_000)
                        }
                        try await writer.write(request)
                    }
                },
                onResponse: { response in
                    var previous: UInt64? = nil
                    var firstMessageNanos: UInt64? = nil
                    var samples: [Int64] = []
                    samples.reserveCapacity(messages - 1)
                    var received = 0

                    for try await message in response.messages {
                        let now = monotonicNanoseconds()
                        if let previous {
                            samples.append(Int64(now - previous))
                        }
                        if firstMessageNanos == nil {
                            firstMessageNanos = now
                        }
                        previous = now
                        try validateResponse(message, expectedBytes: parameters.responseBytes)
                        received += 1
                    }
                    guard received == messages else {
                        throw UnexpectedStreamMessageCount(expected: messages, actual: received)
                    }
                    return StreamMeasurement(
                        requestMessages: Int64(messages),
                        responseMessages: Int64(messages),
                        applicationRequestBytes: Int64(parameters.requestBytes) * Int64(messages),
                        applicationResponseBytes: Int64(parameters.responseBytes) * Int64(messages),
                        latencySamples: samples,
                        timeToFirstResponseNanoseconds: Int64(firstMessageNanos! - streamStarted),
                        streamDurationNanoseconds: nil,
                        finalResponseLatencyNanoseconds: nil
                    )
                }
            )
        }
    }
}

private func serverThroughputCases() -> [BenchmarkCase] {
    [
        streamingBenchmarkCase(
            name: "1k",
            warmupOperations: 200,
            operations: 40_000,
            concurrency: 1,
            requestBytes: streamControlPayloadBytes,
            responseBytes: 1_024
        ),
        streamingBenchmarkCase(
            name: "64k",
            warmupOperations: 20,
            operations: 2_000,
            concurrency: 1,
            requestBytes: streamControlPayloadBytes,
            responseBytes: 64 * 1_024
        ),
        streamingBenchmarkCase(
            name: "1m",
            warmupOperations: 5,
            operations: 256,
            concurrency: 1,
            requestBytes: streamControlPayloadBytes,
            responseBytes: 1_024 * 1_024
        ),
    ]
}

private func clientThroughputCases() -> [BenchmarkCase] {
    [
        streamingBenchmarkCase(
            name: "1k",
            warmupOperations: 200,
            operations: 40_000,
            concurrency: 1,
            requestBytes: 1_024,
            responseBytes: streamControlPayloadBytes
        ),
        streamingBenchmarkCase(
            name: "64k",
            warmupOperations: 20,
            operations: 2_000,
            concurrency: 1,
            requestBytes: 64 * 1_024,
            responseBytes: streamControlPayloadBytes
        ),
        streamingBenchmarkCase(
            name: "1m",
            warmupOperations: 5,
            operations: 256,
            concurrency: 1,
            requestBytes: 1_024 * 1_024,
            responseBytes: streamControlPayloadBytes
        ),
    ]
}

private func messageOverheadCases() -> [BenchmarkCase] {
    [
        streamingBenchmarkCase(name: "1k", warmupOperations: 128, operations: 64 * 1_024, concurrency: 1, requestBytes: 1_024),
        streamingBenchmarkCase(name: "64k", warmupOperations: 16, operations: 1_024, concurrency: 1, requestBytes: 64 * 1_024),
        streamingBenchmarkCase(name: "1m", warmupOperations: 2, operations: 64, concurrency: 1, requestBytes: 1_024 * 1_024),
        streamingBenchmarkCase(
            name: "near-4m",
            warmupOperations: 1,
            operations: 17,
            concurrency: 1,
            requestBytes: 4 * 1_024 * 1_024 - 1_024
        ),
    ]
}

// StreamingFromServer is unbounded, so completing each stream cancels its RPC. SwiftNIO retains a
// bounded number of recently reset HTTP/2 streams and can close the connection when late frames
// arrive for an evicted stream. Stop at 16 so the warmup does not exhaust that allowance.
private func streamConcurrencyCases() -> [BenchmarkCase] {
    [1, 2, 4, 8, 16].map { concurrency in
        streamingBenchmarkCase(
            name: "c\(concurrency)",
            warmupOperations: max(200, concurrency * 8),
            operations: max(16_384, concurrency * 512),
            concurrency: concurrency,
            requestBytes: 64,
            responseBytes: 1_024
        )
    }
}

private func streamingBenchmarkCase(
    name: String,
    warmupOperations: Int,
    operations: Int,
    concurrency: Int,
    requestBytes: Int = 0,
    responseBytes: Int = 0
) -> BenchmarkCase {
    BenchmarkCase(
        name: name,
        parameters: try! BenchmarkParameters(
            warmupOperations: warmupOperations,
            operations: operations,
            concurrency: concurrency,
            requestBytes: requestBytes,
            responseBytes: responseBytes
        )
    )
}

func streamingRequest(requestBytes: Int, responseBytes: Int) -> Grpc_Testing_SimpleRequest {
    unaryRequest(requestBytes: requestBytes, responseBytes: responseBytes)
}

private func validateResponse(_ response: Grpc_Testing_SimpleResponse, expectedBytes: Int) throws {
    guard response.payload.body.count == expectedBytes else {
        throw UnexpectedStreamingResponseSize(expected: expectedBytes, actual: response.payload.body.count)
    }
}

private final class SendLatencyRecorder: @unchecked Sendable {
    struct Snapshot: Sendable {
        let samples: [Int64]
        let finalMessageSent: UInt64
    }

    private let lock = NSLock()
    private var samples: [Int64]
    private var finalMessageSent: UInt64 = 0

    init(capacity: Int) {
        self.samples = []
        self.samples.reserveCapacity(capacity)
    }

    func record(_ nanoseconds: UInt64) {
        self.lock.lock()
        self.samples.append(Int64(nanoseconds))
        self.finalMessageSent = monotonicNanoseconds()
        self.lock.unlock()
    }

    func snapshot() -> Snapshot {
        self.lock.lock()
        defer { self.lock.unlock() }
        return Snapshot(samples: self.samples, finalMessageSent: self.finalMessageSent)
    }
}

private struct UnexpectedStreamingResponseSize: Error, CustomStringConvertible, Sendable {
    let expected: Int
    let actual: Int

    var description: String {
        "Expected \(self.expected) response bytes, got \(self.actual)"
    }
}

private struct UnexpectedStreamMessageCount: Error, CustomStringConvertible, Sendable {
    let expected: Int
    let actual: Int

    var description: String {
        "Expected \(self.expected) stream messages, got \(self.actual)"
    }
}

private struct InvalidFixedStreamSize: Error, Sendable {}

private let messageOverheadTotalBytes: Int64 = 64 * 1_024 * 1_024
private let streamControlPayloadBytes = 64
