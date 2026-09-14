/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import GRPCCore
import GRPCNIOTransportHTTP2TransportServices

typealias BenchmarkClient = GRPCClient<HTTP2ClientTransport.TransportServices>

struct BenchmarkOverrides: Equatable, Sendable {
    var warmupCalls: Int?
    var calls: Int?
    var concurrency: Int?
    var requestBytes: Int?
    var responseBytes: Int?

    func applying(to parameters: BenchmarkParameters) throws -> BenchmarkParameters {
        try BenchmarkParameters(
            warmupCalls: self.warmupCalls ?? parameters.warmupCalls,
            calls: self.calls ?? parameters.calls,
            concurrency: self.concurrency ?? parameters.concurrency,
            requestBytes: self.requestBytes ?? parameters.requestBytes,
            responseBytes: self.responseBytes ?? parameters.responseBytes
        )
    }
}

struct BenchmarkParameters: Equatable, Sendable {
    let warmupCalls: Int
    let calls: Int
    let concurrency: Int
    let requestBytes: Int
    let responseBytes: Int

    init(
        warmupCalls: Int,
        calls: Int,
        concurrency: Int,
        requestBytes: Int,
        responseBytes: Int
    ) throws {
        guard warmupCalls >= 0 else { throw BenchmarkConfigurationError("--warmup must be at least 0") }
        guard calls > 0 else { throw BenchmarkConfigurationError("--calls must be greater than 0") }
        guard concurrency > 0 else { throw BenchmarkConfigurationError("--concurrency must be greater than 0") }
        guard requestBytes >= 0 else {
            throw BenchmarkConfigurationError("--request-bytes must be at least 0")
        }
        guard responseBytes >= 0 else {
            throw BenchmarkConfigurationError("--response-bytes must be at least 0")
        }
        guard responseBytes <= Int(Int32.max) else {
            throw BenchmarkConfigurationError("--response-bytes must fit in a protobuf int32")
        }

        self.warmupCalls = warmupCalls
        self.calls = calls
        self.concurrency = concurrency
        self.requestBytes = requestBytes
        self.responseBytes = responseBytes
    }
}

struct BenchmarkConfigurationError: Error, Equatable, CustomStringConvertible, Sendable {
    let description: String

    init(_ description: String) {
        self.description = description
    }
}

protocol Benchmark: Sendable {
    var name: String { get }
    var description: String { get }
    var defaults: BenchmarkParameters { get }

    func run(client: BenchmarkClient, parameters: BenchmarkParameters) async throws -> BenchmarkResult
}

struct BenchmarkRegistry: Sendable {
    let all: [any Benchmark]

    init(_ benchmarks: [any Benchmark]) {
        let names = benchmarks.map(\.name)
        precondition(Set(names).count == names.count, "Benchmark names must be unique")
        self.all = benchmarks.sorted { $0.name < $1.name }
    }

    func find(_ name: String) -> (any Benchmark)? {
        self.all.first { $0.name == name }
    }
}

struct MeasuredCall: Sendable {
    let applicationBytes: Int64
    let execute: @Sendable () async throws -> Void
}

struct CallBenchmark: Benchmark {
    let name: String
    let description: String
    let defaults: BenchmarkParameters
    let prepare: @Sendable (BenchmarkClient, BenchmarkParameters) -> MeasuredCall

    func run(client: BenchmarkClient, parameters: BenchmarkParameters) async throws -> BenchmarkResult {
        let call = self.prepare(client, parameters)

        _ = try await executeCalls(
            count: parameters.warmupCalls,
            concurrency: parameters.concurrency,
            recordLatency: false,
            call: call.execute
        )

        let clock = ContinuousClock()
        let started = clock.now
        let samples = try await executeCalls(
            count: parameters.calls,
            concurrency: parameters.concurrency,
            recordLatency: true,
            call: call.execute
        )
        let elapsed = started.duration(to: clock.now)

        return BenchmarkResult(
            benchmarkName: self.name,
            platform: currentPlatform,
            parameters: parameters,
            elapsed: elapsed,
            applicationBytes: call.applicationBytes * Int64(parameters.calls),
            latency: LatencyStatistics(samples: samples)
        )
    }
}

private func executeCalls(
    count: Int,
    concurrency: Int,
    recordLatency: Bool,
    call: @escaping @Sendable () async throws -> Void
) async throws -> [UInt64] {
    guard count > 0 else { return [] }

    let workerCount = min(count, concurrency)
    let baseCallsPerWorker = count / workerCount
    let workersWithExtraCall = count % workerCount

    return try await withThrowingTaskGroup(of: [UInt64].self) { group in
        for workerIndex in 0..<workerCount {
            let workerCalls = baseCallsPerWorker + (workerIndex < workersWithExtraCall ? 1 : 0)
            group.addTask {
                var samples: [UInt64] = []
                if recordLatency {
                    samples.reserveCapacity(workerCalls)
                }

                let clock = ContinuousClock()
                for _ in 0..<workerCalls {
                    if recordLatency {
                        let started = clock.now
                        try await call()
                        samples.append(started.duration(to: clock.now).nanoseconds)
                    } else {
                        try await call()
                    }
                }
                return samples
            }
        }

        var samples: [UInt64] = []
        if recordLatency {
            samples.reserveCapacity(count)
        }
        for try await workerSamples in group {
            samples.append(contentsOf: workerSamples)
        }
        return samples
    }
}

struct LatencyStatistics: Equatable, Sendable {
    let minimumNanoseconds: UInt64
    let meanNanoseconds: UInt64
    let p50Nanoseconds: UInt64
    let p90Nanoseconds: UInt64
    let p95Nanoseconds: UInt64
    let p99Nanoseconds: UInt64
    let p999Nanoseconds: UInt64
    let maximumNanoseconds: UInt64

    init(samples: [UInt64]) {
        precondition(!samples.isEmpty, "At least one latency sample is required")
        let sorted = samples.sorted()

        func percentile(_ value: Double) -> UInt64 {
            let index = Int((value * Double(sorted.count - 1)).rounded())
            return sorted[index]
        }

        self.minimumNanoseconds = sorted[0]
        self.meanNanoseconds = UInt64(
            (samples.reduce(0.0) { $0 + Double($1) } / Double(samples.count)).rounded(.towardZero)
        )
        self.p50Nanoseconds = percentile(0.50)
        self.p90Nanoseconds = percentile(0.90)
        self.p95Nanoseconds = percentile(0.95)
        self.p99Nanoseconds = percentile(0.99)
        self.p999Nanoseconds = percentile(0.999)
        self.maximumNanoseconds = sorted[sorted.count - 1]
    }
}

struct BenchmarkResult: Sendable {
    let benchmarkName: String
    let platform: String
    let parameters: BenchmarkParameters
    let elapsed: Duration
    let applicationBytes: Int64
    let latency: LatencyStatistics

    var callsPerSecond: Double {
        Double(self.parameters.calls) / self.elapsed.seconds
    }

    var applicationBytesPerSecond: Double {
        Double(self.applicationBytes) / self.elapsed.seconds
    }
}

extension Duration {
    var seconds: Double {
        let components = self.components
        return Double(components.seconds) + Double(components.attoseconds) / 1_000_000_000_000_000_000
    }

    var nanoseconds: UInt64 {
        UInt64(max(0, (self.seconds * 1_000_000_000).rounded()))
    }
}

private let currentPlatform: String = {
    #if os(iOS) && targetEnvironment(simulator) && arch(arm64)
    return "ios-simulator-arm64"
    #elseif os(iOS) && targetEnvironment(simulator)
    return "ios-simulator"
    #elseif os(iOS)
    return "ios"
    #elseif os(macOS)
    return "macos"
    #else
    return "swift"
    #endif
}()
