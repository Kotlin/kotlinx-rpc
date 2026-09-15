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
}
