/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCProtobuf

struct StreamMeasurement: Sendable {
    let requestMessages: Int64
    let responseMessages: Int64
    let applicationBytes: Int64
    let latencySamples: [UInt64]
    let timeToFirstResponseNanoseconds: UInt64?
    let finalResponseLatencyNanoseconds: UInt64?

    init(
        requestMessages: Int64,
        responseMessages: Int64,
        applicationBytes: Int64,
        latencySamples: [UInt64],
        timeToFirstResponseNanoseconds: UInt64? = nil,
        finalResponseLatencyNanoseconds: UInt64? = nil
    ) {
        self.requestMessages = requestMessages
        self.responseMessages = responseMessages
        self.applicationBytes = applicationBytes
        self.latencySamples = latencySamples
        self.timeToFirstResponseNanoseconds = timeToFirstResponseNanoseconds
        self.finalResponseLatencyNanoseconds = finalResponseLatencyNanoseconds
    }
}

struct StreamingBenchmark: Benchmark {
    let name: String
    let description: String
    let cases: [BenchmarkCase]
    let prepare: @Sendable (BenchmarkClient) -> @Sendable (Int, BenchmarkParameters) async throws -> StreamMeasurement

    func run(
        client: BenchmarkClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters
    ) async throws -> BenchmarkResult {
        let executeStream = self.prepare(client)
        if parameters.warmupCalls > 0 {
            _ = try await executeStreams(
                messageCount: parameters.warmupCalls,
                parameters: parameters,
                executeStream: executeStream
            )
        }

        let clock = ContinuousClock()
        let started = clock.now
        let measurements = try await executeStreams(
            messageCount: parameters.calls,
            parameters: parameters,
            executeStream: executeStream
        )
        let elapsed = started.duration(to: clock.now)
        let samples = measurements.flatMap(\.latencySamples)

        return BenchmarkResult(
            benchmarkName: self.name,
            caseName: benchmarkCase.name,
            implementationName: "swift",
            platform: currentPlatform,
            parameters: parameters,
            elapsed: elapsed,
            applicationBytes: measurements.reduce(0) { $0 + $1.applicationBytes },
            latency: LatencyStatistics(samples: samples),
            requestMessages: measurements.reduce(0) { $0 + $1.requestMessages },
            responseMessages: measurements.reduce(0) { $0 + $1.responseMessages },
            timeToFirstResponse: measurements.averageDuration(\.timeToFirstResponseNanoseconds),
            finalResponseLatency: measurements.averageDuration(\.finalResponseLatencyNanoseconds)
        )
    }
}

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

private func executeStreams(
    messageCount: Int,
    parameters: BenchmarkParameters,
    executeStream: @escaping @Sendable (Int, BenchmarkParameters) async throws -> StreamMeasurement
) async throws -> [StreamMeasurement] {
    let streamCount = min(messageCount, parameters.concurrency)
    let messagesPerStream = messageCount / streamCount
    let streamsWithExtraMessage = messageCount % streamCount

    return try await withThrowingTaskGroup(of: StreamMeasurement.self) { group in
        for streamIndex in 0..<streamCount {
            let messages = messagesPerStream + (streamIndex < streamsWithExtraMessage ? 1 : 0)
            group.addTask {
                try await executeStream(messages, parameters)
            }
        }

        var measurements: [StreamMeasurement] = []
        measurements.reserveCapacity(streamCount)
        for try await measurement in group {
            measurements.append(measurement)
        }
        return measurements
    }
}

private func serverStreamingBenchmark(
    name: String,
    description: String,
    cases: [BenchmarkCase]
) -> any Benchmark {
    StreamingBenchmark(name: name, description: description, cases: cases) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters in
            let request = streamingRequest(
                requestBytes: parameters.requestBytes,
                responseBytes: parameters.responseBytes
            )
            let streamStarted = monotonicNanoseconds()

            return try await service.streamingFromServer(request) { response in
                var previousMessage = streamStarted
                var samples: [UInt64] = []
                samples.reserveCapacity(messages)

                for try await message in response.messages {
                    let now = monotonicNanoseconds()
                    samples.append(now - previousMessage)
                    previousMessage = now
                    try validateResponse(message, expectedBytes: parameters.responseBytes)
                    if samples.count == messages { break }
                }
                guard samples.count == messages else {
                    throw UnexpectedStreamMessageCount(expected: messages, actual: samples.count)
                }
                return StreamMeasurement(
                    requestMessages: 1,
                    responseMessages: Int64(messages),
                    applicationBytes: Int64(parameters.requestBytes) +
                        Int64(parameters.responseBytes) * Int64(messages),
                    latencySamples: samples,
                    timeToFirstResponseNanoseconds: samples[0]
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
    StreamingBenchmark(name: name, description: description, cases: cases) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters in
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
                applicationBytes: (measuredFixedTotal ?? Int64(parameters.requestBytes) * Int64(messages)) +
                    Int64(parameters.responseBytes),
                latencySamples: snapshot.samples,
                timeToFirstResponseNanoseconds: responseReceived - streamStarted,
                finalResponseLatencyNanoseconds: responseReceived - snapshot.finalMessageSent
            )
        }
    }
}

private func pingPongBenchmark() -> any Benchmark {
    StreamingBenchmark(
        name: "bidi-ping-pong",
        description: "Strict send-one/receive-one bidirectional streaming latency",
        cases: [
            streamingBenchmarkCase(name: "empty", warmupCalls: 100, calls: 10_000, concurrency: 1),
            streamingBenchmarkCase(
                name: "1k",
                warmupCalls: 100,
                calls: 10_000,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 1_024
            ),
        ]
    ) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters in
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
                        try await writer.write(request)
                    }
                },
                onResponse: { response in
                    defer { nextRequest.continuation.finish() }
                    var sentAt = firstRequestStarted
                    var samples: [UInt64] = []
                    samples.reserveCapacity(messages)

                    for try await message in response.messages {
                        let now = monotonicNanoseconds()
                        samples.append(now - sentAt)
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
                        applicationBytes: Int64(parameters.requestBytes + parameters.responseBytes) *
                            Int64(messages),
                        latencySamples: samples,
                        timeToFirstResponseNanoseconds: samples[0]
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
                warmupCalls: 100,
                calls: 10_000,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 1_024
            ),
            streamingBenchmarkCase(
                name: "balanced-64k",
                warmupCalls: 10,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 64 * 1_024,
                responseBytes: 64 * 1_024
            ),
            streamingBenchmarkCase(
                name: "upload-heavy",
                warmupCalls: 10,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 64 * 1_024,
                responseBytes: 1_024
            ),
            streamingBenchmarkCase(
                name: "download-heavy",
                warmupCalls: 10,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 64 * 1_024
            ),
        ]
    ) { client in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        return { messages, parameters in
            let request = streamingRequest(
                requestBytes: parameters.requestBytes,
                responseBytes: parameters.responseBytes
            )
            let streamStarted = monotonicNanoseconds()

            return try await service.streamingCall(
                requestProducer: { writer in
                    for _ in 0..<messages {
                        try await writer.write(request)
                    }
                },
                onResponse: { response in
                    var previousMessage = streamStarted
                    var samples: [UInt64] = []
                    samples.reserveCapacity(messages)

                    for try await message in response.messages {
                        let now = monotonicNanoseconds()
                        samples.append(now - previousMessage)
                        previousMessage = now
                        try validateResponse(message, expectedBytes: parameters.responseBytes)
                    }
                    guard samples.count == messages else {
                        throw UnexpectedStreamMessageCount(expected: messages, actual: samples.count)
                    }
                    return StreamMeasurement(
                        requestMessages: Int64(messages),
                        responseMessages: Int64(messages),
                        applicationBytes: Int64(parameters.requestBytes + parameters.responseBytes) *
                            Int64(messages),
                        latencySamples: samples,
                        timeToFirstResponseNanoseconds: samples[0]
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
            warmupCalls: 100,
            calls: 10_000,
            concurrency: 1,
            requestBytes: streamControlPayloadBytes,
            responseBytes: 1_024
        ),
        streamingBenchmarkCase(
            name: "64k",
            warmupCalls: 10,
            calls: 1_000,
            concurrency: 1,
            requestBytes: streamControlPayloadBytes,
            responseBytes: 64 * 1_024
        ),
        streamingBenchmarkCase(
            name: "1m",
            warmupCalls: 2,
            calls: 64,
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
            warmupCalls: 100,
            calls: 10_000,
            concurrency: 1,
            requestBytes: 1_024,
            responseBytes: streamControlPayloadBytes
        ),
        streamingBenchmarkCase(
            name: "64k",
            warmupCalls: 10,
            calls: 1_000,
            concurrency: 1,
            requestBytes: 64 * 1_024,
            responseBytes: streamControlPayloadBytes
        ),
        streamingBenchmarkCase(
            name: "1m",
            warmupCalls: 2,
            calls: 64,
            concurrency: 1,
            requestBytes: 1_024 * 1_024,
            responseBytes: streamControlPayloadBytes
        ),
    ]
}

private func messageOverheadCases() -> [BenchmarkCase] {
    [
        streamingBenchmarkCase(name: "1k", warmupCalls: 128, calls: 64 * 1_024, concurrency: 1, requestBytes: 1_024),
        streamingBenchmarkCase(name: "64k", warmupCalls: 16, calls: 1_024, concurrency: 1, requestBytes: 64 * 1_024),
        streamingBenchmarkCase(name: "1m", warmupCalls: 2, calls: 64, concurrency: 1, requestBytes: 1_024 * 1_024),
        streamingBenchmarkCase(
            name: "near-4m",
            warmupCalls: 1,
            calls: 17,
            concurrency: 1,
            requestBytes: 4 * 1_024 * 1_024 - 1_024
        ),
    ]
}

// StreamingFromServer is unbounded, so completing each measurement resets its stream. SwiftNIO
// retains a bounded number of recently reset HTTP/2 streams and can close the connection when late
// frames arrive for an evicted stream. Stop at 16 so the warmup does not exhaust that allowance.
private func streamConcurrencyCases() -> [BenchmarkCase] {
    [1, 2, 4, 8, 16].map { concurrency in
        streamingBenchmarkCase(
            name: "c\(concurrency)",
            warmupCalls: max(128, concurrency * 4),
            calls: max(8_192, concurrency * 128),
            concurrency: concurrency,
            requestBytes: 64,
            responseBytes: 1_024
        )
    }
}

private func streamingBenchmarkCase(
    name: String,
    warmupCalls: Int,
    calls: Int,
    concurrency: Int,
    requestBytes: Int = 0,
    responseBytes: Int = 0
) -> BenchmarkCase {
    BenchmarkCase(
        name: name,
        parameters: try! BenchmarkParameters(
            warmupCalls: warmupCalls,
            calls: calls,
            concurrency: concurrency,
            requestBytes: requestBytes,
            responseBytes: responseBytes
        )
    )
}

private func streamingRequest(requestBytes: Int, responseBytes: Int) -> Grpc_Testing_SimpleRequest {
    var payload = Grpc_Testing_Payload()
    payload.type = .compressable
    payload.body = Data((0..<requestBytes).lazy.map { UInt8($0 % 251) })

    var request = Grpc_Testing_SimpleRequest()
    request.responseType = .compressable
    request.responseSize = Int32(responseBytes)
    request.payload = payload
    return request
}

private func validateResponse(_ response: Grpc_Testing_SimpleResponse, expectedBytes: Int) throws {
    guard response.payload.body.count == expectedBytes else {
        throw UnexpectedStreamingResponseSize(expected: expectedBytes, actual: response.payload.body.count)
    }
}

private func monotonicNanoseconds() -> UInt64 {
    DispatchTime.now().uptimeNanoseconds
}

private extension Array where Element == StreamMeasurement {
    func averageDuration(_ keyPath: KeyPath<StreamMeasurement, UInt64?>) -> Duration? {
        let values = self.compactMap { $0[keyPath: keyPath] }
        guard !values.isEmpty else { return nil }
        let average = values.reduce(0.0) { $0 + Double($1) } / Double(values.count)
        return .nanoseconds(Int64(average))
    }
}

private final class SendLatencyRecorder: @unchecked Sendable {
    struct Snapshot: Sendable {
        let samples: [UInt64]
        let finalMessageSent: UInt64
    }

    private let lock = NSLock()
    private var samples: [UInt64]
    private var finalMessageSent: UInt64 = 0

    init(capacity: Int) {
        self.samples = []
        self.samples.reserveCapacity(capacity)
    }

    func record(_ nanoseconds: UInt64) {
        self.lock.lock()
        self.samples.append(nanoseconds)
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
