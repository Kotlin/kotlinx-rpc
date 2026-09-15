/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import XCTest
@testable import SwiftGrpcBenchmarkClient

final class BenchmarkTests: XCTestCase {
    func testUnaryBenchmarkDefaultsMatchKotlinClient() throws {
        let registry = BenchmarkRegistry(unaryBenchmarks())

        XCTAssertEqual(
            registry.all.map(\.name),
            [
                "unary-concurrency-sweep",
                "unary-latency",
                "unary-payload-sweep",
                "unary-throughput",
            ]
        )
        XCTAssertEqual(
            registry.find("unary-latency")?.cases,
            [
                BenchmarkCase(
                    parameters: try BenchmarkParameters(
                        warmupCalls: 100,
                        calls: 1_000,
                        concurrency: 1,
                        requestBytes: 0,
                        responseBytes: 0
                    )
                )
            ]
        )
        XCTAssertEqual(
            registry.find("unary-throughput")?.cases,
            [
                BenchmarkCase(
                    parameters: try BenchmarkParameters(
                        warmupCalls: 100,
                        calls: 10_000,
                        concurrency: 16,
                        requestBytes: 1_024,
                        responseBytes: 1_024
                    )
                )
            ]
        )
    }

    func testPayloadSweepCasesMatchKotlinClient() throws {
        let cases = try XCTUnwrap(BenchmarkRegistry(unaryBenchmarks()).find("unary-payload-sweep")?.cases)

        XCTAssertEqual(
            cases.map(\.name),
            [
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
            ]
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "upload-1m" })?.parameters,
            try BenchmarkParameters(
                warmupCalls: 5,
                calls: 200,
                concurrency: 1,
                requestBytes: 1_024 * 1_024,
                responseBytes: 64
            )
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "download-64k" })?.parameters,
            try BenchmarkParameters(
                warmupCalls: 20,
                calls: 2_000,
                concurrency: 1,
                requestBytes: 64,
                responseBytes: 64 * 1_024
            )
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "upload-near-4m" })?.parameters,
            try BenchmarkParameters(
                warmupCalls: 2,
                calls: 50,
                concurrency: 1,
                requestBytes: 4 * 1_024 * 1_024 - 1_024,
                responseBytes: 64
            )
        )
    }

    func testConcurrencySweepCasesMatchKotlinClient() throws {
        let cases = try XCTUnwrap(BenchmarkRegistry(unaryBenchmarks()).find("unary-concurrency-sweep")?.cases)

        XCTAssertEqual(cases.count, 16)
        XCTAssertEqual(
            cases.first(where: { $0.name == "empty-c1" })?.parameters,
            try BenchmarkParameters(
                warmupCalls: 100,
                calls: 10_000,
                concurrency: 1,
                requestBytes: 0,
                responseBytes: 0
            )
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "1k-c128" })?.parameters,
            try BenchmarkParameters(
                warmupCalls: 1_280,
                calls: 128_000,
                concurrency: 128,
                requestBytes: 1_024,
                responseBytes: 1_024
            )
        )
    }

    func testStreamingBenchmarkCasesMatchKotlinClient() throws {
        let registry = BenchmarkRegistry(streamingBenchmarks())

        XCTAssertEqual(
            registry.all.map(\.name),
            [
                "bidi-full-duplex",
                "bidi-ping-pong",
                "client-streaming-throughput",
                "server-streaming-throughput",
                "stream-concurrency-sweep",
                "stream-message-overhead",
            ]
        )
        let overheadCases = try XCTUnwrap(registry.find("stream-message-overhead")?.cases)
        XCTAssertEqual(overheadCases.map(\.name), ["1k", "64k", "1m", "near-4m"])
        XCTAssertEqual(
            overheadCases.last?.parameters,
            try BenchmarkParameters(
                warmupCalls: 1,
                calls: 17,
                concurrency: 1,
                requestBytes: 4 * 1_024 * 1_024 - 1_024,
                responseBytes: 0
            )
        )
        let largestOverheadCase = try XCTUnwrap(overheadCases.last?.parameters)
        XCTAssertEqual(
            Int64(largestOverheadCase.requestBytes) * Int64(largestOverheadCase.calls - 1) + 16 * 1_024,
            64 * 1_024 * 1_024
        )
        XCTAssertEqual(
            registry.find("stream-concurrency-sweep")?.cases.map(\.name),
            ["c1", "c2", "c4", "c8", "c16", "c32"]
        )
        XCTAssertEqual(
            registry.find("server-streaming-throughput")?.cases.first { $0.name == "64k" }?.parameters,
            try BenchmarkParameters(
                warmupCalls: 10,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 64,
                responseBytes: 64 * 1_024
            )
        )
        XCTAssertEqual(
            registry.find("client-streaming-throughput")?.cases.first { $0.name == "64k" }?.parameters,
            try BenchmarkParameters(
                warmupCalls: 10,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 64 * 1_024,
                responseBytes: 64
            )
        )
    }

    func testAppliesParameterOverrides() throws {
        let defaults = try BenchmarkParameters(
            warmupCalls: 1,
            calls: 2,
            concurrency: 3,
            requestBytes: 4,
            responseBytes: 5
        )
        let overrides = BenchmarkOverrides(calls: 20, responseBytes: 50)

        XCTAssertEqual(
            try overrides.applying(to: defaults),
            try BenchmarkParameters(
                warmupCalls: 1,
                calls: 20,
                concurrency: 3,
                requestBytes: 4,
                responseBytes: 50
            )
        )
        XCTAssertThrowsError(try BenchmarkOverrides(concurrency: 0).applying(to: defaults))
    }

    func testComputesLatencyPercentiles() {
        let statistics = LatencyStatistics(samples: [1, 2, 3, 4, 100])

        XCTAssertEqual(statistics.minimumNanoseconds, 1)
        XCTAssertEqual(statistics.meanNanoseconds, 22)
        XCTAssertEqual(statistics.p50Nanoseconds, 3)
        XCTAssertEqual(statistics.p90Nanoseconds, 100)
        XCTAssertEqual(statistics.maximumNanoseconds, 100)
    }

    func testFormatsApplicationThroughput() {
        XCTAssertEqual(999.0.bytesPerSecond, "999.00 B/s")
        XCTAssertEqual(1_500.0.bytesPerSecond, "1.50 KB/s")
        XCTAssertEqual(2_500_000.0.bytesPerSecond, "2.50 MB/s")
        XCTAssertEqual(3_500_000_000.0.bytesPerSecond, "3.50 GB/s")
    }

    func testCSVOutputIncludesCaseAndImplementation() throws {
        let parameters = try BenchmarkParameters(
            warmupCalls: 0,
            calls: 1,
            concurrency: 1,
            requestBytes: 0,
            responseBytes: 0
        )
        let result = BenchmarkResult(
            benchmarkName: "example",
            caseName: "example-case",
            implementationName: "swift",
            platform: "macos",
            parameters: parameters,
            elapsed: .nanoseconds(1),
            applicationBytes: 0,
            latency: LatencyStatistics(samples: [1])
        )

        let output = render(
            results: [result],
            target: ServerTarget(host: "localhost", port: 50051),
            format: .csv
        )

        XCTAssertTrue(output.contains("benchmark,case,implementation,platform"))
        XCTAssertTrue(output.contains("example,example-case,swift,macos"))
    }

    func testCSVOutputIncludesStreamingMeasurements() throws {
        let result = BenchmarkResult(
            benchmarkName: "streaming-example",
            caseName: "default",
            implementationName: "swift",
            platform: "macos",
            parameters: try BenchmarkParameters(
                warmupCalls: 0,
                calls: 10,
                concurrency: 1,
                requestBytes: 1_024,
                responseBytes: 64
            ),
            elapsed: .seconds(1),
            applicationBytes: 10_304,
            latency: LatencyStatistics(samples: [1]),
            requestMessages: 10,
            responseMessages: 1,
            timeToFirstResponse: .nanoseconds(2),
            finalResponseLatency: .nanoseconds(3)
        )

        let output = render(
            results: [result],
            target: ServerTarget(host: "localhost", port: 50051),
            format: .csv
        )

        XCTAssertTrue(output.contains("request_messages,response_messages,messages_per_second"))
        XCTAssertTrue(output.contains(",10,1,11.0000,0.002,0.003"))
    }
}
