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
                        warmupOperations: 200,
                        operations: 10_000,
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
                        warmupOperations: 200,
                        operations: 100_000,
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
                warmupOperations: 10,
                operations: 600,
                concurrency: 1,
                requestBytes: 1_024 * 1_024,
                responseBytes: 64
            )
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "download-64k" })?.parameters,
            try BenchmarkParameters(
                warmupOperations: 50,
                operations: 3_000,
                concurrency: 1,
                requestBytes: 64,
                responseBytes: 64 * 1_024
            )
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "upload-near-4m" })?.parameters,
            try BenchmarkParameters(
                warmupOperations: 5,
                operations: 150,
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
                warmupOperations: 200,
                operations: 20_000,
                concurrency: 1,
                requestBytes: 0,
                responseBytes: 0
            )
        )
        XCTAssertEqual(
            cases.first(where: { $0.name == "1k-c128" })?.parameters,
            try BenchmarkParameters(
                warmupOperations: 2_560,
                operations: 256_000,
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
                warmupOperations: 1,
                operations: 17,
                concurrency: 1,
                requestBytes: 4 * 1_024 * 1_024 - 1_024,
                responseBytes: 0
            )
        )
        let largestOverheadCase = try XCTUnwrap(overheadCases.last?.parameters)
        XCTAssertEqual(
            Int64(largestOverheadCase.requestBytes) * Int64(largestOverheadCase.operations - 1) + 16 * 1_024,
            64 * 1_024 * 1_024
        )
        XCTAssertEqual(
            registry.find("stream-concurrency-sweep")?.cases.map(\.name),
            ["c1", "c2", "c4", "c8", "c16"]
        )
        XCTAssertEqual(
            registry.find("stream-concurrency-sweep")?.cases.last?.parameters,
            try BenchmarkParameters(
                warmupOperations: 200,
                operations: 16_384,
                concurrency: 16,
                requestBytes: 64,
                responseBytes: 1_024
            )
        )
        XCTAssertEqual(
            registry.find("server-streaming-throughput")?.cases.first { $0.name == "64k" }?.parameters,
            try BenchmarkParameters(
                warmupOperations: 20,
                operations: 2_000,
                concurrency: 1,
                requestBytes: 64,
                responseBytes: 64 * 1_024
            )
        )
        XCTAssertEqual(
            registry.find("client-streaming-throughput")?.cases.first { $0.name == "64k" }?.parameters,
            try BenchmarkParameters(
                warmupOperations: 20,
                operations: 2_000,
                concurrency: 1,
                requestBytes: 64 * 1_024,
                responseBytes: 64
            )
        )
    }

    func testLatencyMetricPerFamilyMatchesKotlinClient() {
        let registry = BenchmarkRegistry(unaryBenchmarks() + streamingBenchmarks())

        XCTAssertEqual(registry.find("unary-latency")?.latencyMetric, .callRoundTrip)
        XCTAssertEqual(registry.find("server-streaming-throughput")?.latencyMetric, .messageInterarrival)
        XCTAssertEqual(registry.find("client-streaming-throughput")?.latencyMetric, .requestWriteSuspension)
        XCTAssertEqual(registry.find("bidi-ping-pong")?.latencyMetric, .pingPongRoundTrip)
        XCTAssertEqual(registry.find("bidi-full-duplex")?.latencyMetric, .messageInterarrival)
        for benchmark in registry.all where benchmark.name.hasPrefix("unary") {
            XCTAssertEqual(benchmark.operationUnit, .rpc, benchmark.name)
        }
        for benchmark in registry.all where !benchmark.name.hasPrefix("unary") {
            XCTAssertEqual(benchmark.operationUnit, .message, benchmark.name)
        }
    }

    func testWorkloadMatrixUsesKotlinWireNames() throws {
        let encoder = JSONEncoder()
        encoder.outputFormatting = [.prettyPrinted, .sortedKeys]
        let data = try encoder.encode(BenchmarkCLI().registry.workloadMatrix())
        let json = String(data: data, encoding: .utf8)!

        XCTAssertTrue(json.contains("\"latency_metric\" : \"ping_pong_round_trip\""))
        XCTAssertTrue(json.contains("\"operation_unit\" : \"message\""))
        XCTAssertTrue(json.contains("\"warmup_operations\" : 200"))
        XCTAssertTrue(json.contains("\"case\" : \"symmetric-1m\""))
    }

    func testAppliesParameterOverrides() throws {
        let defaults = try BenchmarkParameters(
            warmupOperations: 1,
            operations: 2,
            concurrency: 3,
            requestBytes: 4,
            responseBytes: 5
        )
        let overrides = BenchmarkOverrides(operations: 20, responseBytes: 50)

        XCTAssertEqual(
            try overrides.applying(to: defaults),
            try BenchmarkParameters(
                warmupOperations: 1,
                operations: 20,
                concurrency: 3,
                requestBytes: 4,
                responseBytes: 50
            )
        )
        XCTAssertThrowsError(try BenchmarkOverrides(concurrency: 0).applying(to: defaults))
    }

    func testComputesLatencyPercentiles() {
        let statistics = LatencyStatistics(samples: [1, 2, 3, 4, 100])

        XCTAssertEqual(statistics.sampleCount, 5)
        XCTAssertEqual(statistics.minimumNanoseconds, 1)
        XCTAssertEqual(statistics.meanNanoseconds, 22)
        XCTAssertEqual(statistics.p50Nanoseconds, 3)
        XCTAssertEqual(statistics.p90Nanoseconds, 100)
        XCTAssertEqual(statistics.maximumNanoseconds, 100)
    }

    func testReportsEmptyStatisticsWithoutSamples() {
        let statistics = LatencyStatistics(samples: [])

        XCTAssertEqual(statistics.sampleCount, 0)
        XCTAssertNil(statistics.minimumNanoseconds)
        XCTAssertNil(statistics.p50Nanoseconds)
        XCTAssertNil(statistics.maximumNanoseconds)
    }

    func testFormatsApplicationThroughput() {
        XCTAssertEqual(999.0.bytesPerSecond, "999.00 B/s")
        XCTAssertEqual(1_500.0.bytesPerSecond, "1.50 KB/s")
        XCTAssertEqual(2_500_000.0.bytesPerSecond, "2.50 MB/s")
        XCTAssertEqual(3_500_000_000.0.bytesPerSecond, "3.50 GB/s")
    }

    func testTrialRecordJsonUsesKotlinSchema() throws {
        let record = TrialRecord(
            implementationName: "swift",
            implementationVersion: nil,
            platform: "ios-simulator-arm64",
            target: "localhost:50051",
            startedAtEpochMs: 1_762_268_000_000,
            connectionReadyNanoseconds: 123_000_000,
            trial: TrialResult(
                benchmarkName: "server-streaming-throughput",
                caseName: "1k",
                parameters: try BenchmarkParameters(
                    warmupOperations: 10,
                    operations: 100,
                    concurrency: 2,
                    requestBytes: 64,
                    responseBytes: 1_024
                ),
                latencyMetric: .messageInterarrival,
                operationUnit: .message,
                injectedDelayMs: 0,
                status: .completed,
                error: nil,
                elapsedNanoseconds: 1_000_000_000,
                operations: 101,
                requestMessages: 1,
                responseMessages: 100,
                applicationRequestBytes: 64,
                applicationResponseBytes: 102_400,
                latencySamplesNanoseconds: [1, 2, 3],
                timeToFirstResponseNanoseconds: [500],
                streamDurationNanoseconds: [999_000_000],
                finalResponseLatencyNanoseconds: [],
                cpuUserNanoseconds: 1_500_000,
                cpuSystemNanoseconds: 500_000,
                cpuTotalNanoseconds: 2_000_000,
                maxRssBytes: 64 * 1_024 * 1_024
            )
        )

        let encoder = JSONEncoder()
        let json = String(data: try encoder.encode(record), encoding: .utf8)!

        XCTAssertTrue(json.contains("\"schema_version\":2"), json)
        XCTAssertTrue(json.contains("\"latency_metric\":\"message_interarrival\""), json)
        XCTAssertTrue(json.contains("\"operation_unit\":\"message\""), json)
        XCTAssertTrue(json.contains("\"load_shape\":\"closed_loop\""), json)
        XCTAssertTrue(json.contains("\"connection_ready_ns\":123000000"), json)
        XCTAssertTrue(json.contains("\"latency_samples_ns\":[1,2,3]"), json)
        XCTAssertTrue(json.contains("\"time_to_first_response_ns\":[500]"), json)
        XCTAssertTrue(json.contains("\"stream_duration_ns\":[999000000]"), json)
        XCTAssertTrue(json.contains("\"final_response_latency_ns\":[]"), json)
        XCTAssertTrue(json.contains("\"warmup_operations\":10"), json)
        XCTAssertTrue(json.contains("\"cpu_user_ns\":1500000"), json)
        XCTAssertTrue(json.contains("\"cpu_system_ns\":500000"), json)
        XCTAssertTrue(json.contains("\"cpu_total_ns\":2000000"), json)
        XCTAssertTrue(json.contains("\"max_rss_bytes\":67108864"), json)
    }

    func testTrialRecordWritesNullsForMissingCounters() throws {
        let record = TrialRecord(
            implementationName: "swift",
            implementationVersion: nil,
            platform: "macos",
            target: "localhost:50051",
            startedAtEpochMs: 0,
            connectionReadyNanoseconds: nil,
            trial: TrialResult(
                benchmarkName: "unary-latency",
                caseName: "default",
                parameters: try! BenchmarkParameters(
                    warmupOperations: 0, operations: 1, concurrency: 1, requestBytes: 0, responseBytes: 0
                ),
                latencyMetric: .callRoundTrip,
                operationUnit: .rpc,
                injectedDelayMs: 0,
                status: .completed,
                error: nil,
                elapsedNanoseconds: 1,
                operations: 1,
                requestMessages: nil,
                responseMessages: nil,
                applicationRequestBytes: 0,
                applicationResponseBytes: 0,
                latencySamplesNanoseconds: [1],
                timeToFirstResponseNanoseconds: [],
                streamDurationNanoseconds: [],
                finalResponseLatencyNanoseconds: []
            )
        )

        let json = String(data: try JSONEncoder().encode(record), encoding: .utf8)!

        XCTAssertTrue(json.contains("\"cpu_user_ns\":null"), json)
        XCTAssertTrue(json.contains("\"cpu_system_ns\":null"), json)
        XCTAssertTrue(json.contains("\"cpu_total_ns\":null"), json)
        XCTAssertTrue(json.contains("\"max_rss_bytes\":null"), json)
    }

    func testCsvOutputUsesCsvV2Header() {
        let record = TrialRecord(
            implementationName: "swift",
            implementationVersion: nil,
            platform: "ios-simulator-arm64",
            target: "localhost:50051",
            startedAtEpochMs: 0,
            connectionReadyNanoseconds: nil,
            trial: TrialResult(
                benchmarkName: "unary-latency",
                caseName: "default",
                parameters: try! BenchmarkParameters(
                    warmupOperations: 0,
                    operations: 1,
                    concurrency: 1,
                    requestBytes: 0,
                    responseBytes: 0
                ),
                latencyMetric: .callRoundTrip,
                operationUnit: .rpc,
                injectedDelayMs: 0,
                status: .completed,
                error: nil,
                elapsedNanoseconds: 1_000_000,
                operations: 1,
                requestMessages: nil,
                responseMessages: nil,
                applicationRequestBytes: 0,
                applicationResponseBytes: 0,
                latencySamplesNanoseconds: [1],
                timeToFirstResponseNanoseconds: [],
                streamDurationNanoseconds: [],
                finalResponseLatencyNanoseconds: []
            )
        )

        let output = renderCSV(record: record)

        XCTAssertTrue(output.hasPrefix("2,swift,ios-simulator-arm64,unary-latency,default,completed,call_round_trip,rpc"))
        XCTAssertTrue(csvHeader.contains("latency_min_us,latency_mean_us,latency_p50_us"))
        XCTAssertTrue(csvHeader.contains("time_to_first_response_mean_us,stream_duration_mean_us"))
    }
}
