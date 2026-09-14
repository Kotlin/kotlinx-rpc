/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import XCTest
@testable import SwiftGrpcBenchmarkClient

final class BenchmarkTests: XCTestCase {
    func testUnaryBenchmarkDefaultsMatchKotlinClient() throws {
        let registry = BenchmarkRegistry(unaryBenchmarks())

        XCTAssertEqual(registry.all.map(\.name), ["unary-latency", "unary-throughput"])
        XCTAssertEqual(
            registry.find("unary-latency")?.defaults,
            try BenchmarkParameters(
                warmupCalls: 100,
                calls: 1_000,
                concurrency: 1,
                requestBytes: 0,
                responseBytes: 0
            )
        )
        XCTAssertEqual(
            registry.find("unary-throughput")?.defaults,
            try BenchmarkParameters(
                warmupCalls: 100,
                calls: 10_000,
                concurrency: 16,
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
}
