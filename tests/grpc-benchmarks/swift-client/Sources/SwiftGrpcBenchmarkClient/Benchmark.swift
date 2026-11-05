/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCNIOTransportHTTP2TransportServices

typealias BenchmarkClient = GRPCClient<HTTP2ClientTransport.TransportServices>

/// The client-side interval that one latency sample measures.
///
/// Every benchmark states its metric, so results never pool samples of different intervals.
enum LatencyMetric: String, Sendable {
    /// One request send to the completed receive of its unary response.
    case callRoundTrip = "call_round_trip"
    /// The receive of one response message to the receive of the next message.
    case messageInterarrival = "message_interarrival"
    /// The start of one request write to the return of the write call.
    case requestWriteSuspension = "request_write_suspension"
    /// One request send to the completed receive of its response on a streaming call.
    case pingPongRoundTrip = "ping_pong_round_trip"
}

/// The unit of the counted operation of a benchmark.
enum OperationUnit: String, Sendable {
    case rpc
    case message
}

/// The load shape of every benchmark in this harness.
let loadShape = "closed_loop"

/// Schema version of the JSON trial record, shared with the Kotlin clients and the analysis tools.
let trialSchemaVersion = 2

/// The process resource counters of one point in time.
///
/// The CPU values are the totals of all threads of the process.
struct ProcessResourceUsage: Sendable {
    let cpuUserNanoseconds: Int64?
    let cpuSystemNanoseconds: Int64?
    let cpuTotalNanoseconds: Int64
    let maxRssBytes: Int64?
}

/// Reads the current process resource counters. Darwin fills the user and system split and the
/// peak resident size in bytes.
func processResourceUsage() -> ProcessResourceUsage? {
    var usage = rusage()
    guard getrusage(RUSAGE_SELF, &usage) == 0 else { return nil }
    let userNanoseconds = Int64(usage.ru_utime.tv_sec) * 1_000_000_000 + Int64(usage.ru_utime.tv_usec) * 1_000
    let systemNanoseconds = Int64(usage.ru_stime.tv_sec) * 1_000_000_000 + Int64(usage.ru_stime.tv_usec) * 1_000
    return ProcessResourceUsage(
        cpuUserNanoseconds: userNanoseconds,
        cpuSystemNanoseconds: systemNanoseconds,
        cpuTotalNanoseconds: userNanoseconds + systemNanoseconds,
        maxRssBytes: Int64(usage.ru_maxrss)
    )
}

/// The CPU time between two resource snapshots, in nanoseconds.
func cpuDelta(before: ProcessResourceUsage?, after: ProcessResourceUsage?) -> ProcessResourceUsage? {
    guard let before, let after else { return nil }
    return ProcessResourceUsage(
        cpuUserNanoseconds: deltaOf(before.cpuUserNanoseconds, after.cpuUserNanoseconds),
        cpuSystemNanoseconds: deltaOf(before.cpuSystemNanoseconds, after.cpuSystemNanoseconds),
        cpuTotalNanoseconds: after.cpuTotalNanoseconds - before.cpuTotalNanoseconds,
        maxRssBytes: nil
    )
}

private func deltaOf(_ before: Int64?, _ after: Int64?) -> Int64? {
    guard let before, let after else { return nil }
    return after - before
}

struct BenchmarkOverrides: Equatable, Sendable {
    var warmupOperations: Int?
    var operations: Int?
    var concurrency: Int?
    var requestBytes: Int?
    var responseBytes: Int?

    func applying(to parameters: BenchmarkParameters) throws -> BenchmarkParameters {
        try BenchmarkParameters(
            warmupOperations: self.warmupOperations ?? parameters.warmupOperations,
            operations: self.operations ?? parameters.operations,
            concurrency: self.concurrency ?? parameters.concurrency,
            requestBytes: self.requestBytes ?? parameters.requestBytes,
            responseBytes: self.responseBytes ?? parameters.responseBytes,
            pauseMs: parameters.pauseMs
        )
    }
}

struct BenchmarkParameters: Sendable {
    let warmupOperations: Int
    let operations: Int
    let concurrency: Int
    let requestBytes: Int
    let responseBytes: Int
    let pauseMs: Int

    init(
        warmupOperations: Int,
        operations: Int,
        concurrency: Int,
        requestBytes: Int,
        responseBytes: Int,
        pauseMs: Int = 0
    ) throws {
        guard warmupOperations >= 0 else { throw BenchmarkConfigurationError("--warmup must be at least 0") }
        guard operations > 0 else { throw BenchmarkConfigurationError("--operations must be greater than 0") }
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

        self.warmupOperations = warmupOperations
        self.operations = operations
        self.concurrency = concurrency
        self.requestBytes = requestBytes
        guard pauseMs >= 0 else { throw BenchmarkConfigurationError("Pause must be at least 0 ms") }
        self.responseBytes = responseBytes
        self.pauseMs = pauseMs
    }

    enum CodingKeys: String, CodingKey {
        case warmupOperations = "warmup_operations"
        case operations
        case concurrency
        case requestBytes = "request_bytes"
        case responseBytes = "response_bytes"
        case pauseMs = "pause_ms"
    }
}

extension BenchmarkParameters: Encodable, Equatable {
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(self.warmupOperations, forKey: .warmupOperations)
        try container.encode(self.operations, forKey: .operations)
        try container.encode(self.concurrency, forKey: .concurrency)
        try container.encode(self.requestBytes, forKey: .requestBytes)
        try container.encode(self.responseBytes, forKey: .responseBytes)
        if self.pauseMs > 0 {
            try container.encode(self.pauseMs, forKey: .pauseMs)
        }
    }
}

struct BenchmarkConfigurationError: Error, Equatable, CustomStringConvertible, Sendable {
    let description: String

    init(_ description: String) {
        self.description = description
    }
}

struct BenchmarkCase: Equatable, Sendable {
    let name: String
    let parameters: BenchmarkParameters

    init(name: String = "default", parameters: BenchmarkParameters) {
        precondition(!name.isEmpty, "Benchmark case name must not be empty")
        self.name = name
        self.parameters = parameters
    }
}

struct BenchmarkWorkload: Encodable, Equatable, Sendable {
    let benchmark: String
    let description: String
    let latencyMetric: String
    let operationUnit: String
    let cases: [BenchmarkCaseEntry]

    enum CodingKeys: String, CodingKey {
        case benchmark
        case description
        case latencyMetric = "latency_metric"
        case operationUnit = "operation_unit"
        case cases
    }
}

struct BenchmarkCaseEntry: Encodable, Equatable, Sendable {
    let name: String
    let parameters: BenchmarkParameters

    enum CodingKeys: String, CodingKey {
        case name = "case"
        case parameters
    }
}

protocol Benchmark: Sendable {
    var name: String { get }
    var description: String { get }
    var cases: [BenchmarkCase] { get }
    var latencyMetric: LatencyMetric { get }
    var operationUnit: OperationUnit { get }

    func run(
        client: BenchmarkClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int
    ) async throws -> TrialResult
}

struct BenchmarkRegistry: Sendable {
    let all: [any Benchmark]

    init(_ benchmarks: [any Benchmark]) {
        let names = benchmarks.map(\.name)
        precondition(Set(names).count == names.count, "Benchmark names must be unique")
        for benchmark in benchmarks {
            precondition(!benchmark.cases.isEmpty, "Benchmark '\(benchmark.name)' has no cases")
            let caseNames = benchmark.cases.map(\.name)
            precondition(
                Set(caseNames).count == caseNames.count,
                "Benchmark '\(benchmark.name)' has duplicate case names"
            )
        }
        self.all = benchmarks.sorted { $0.name < $1.name }
    }

    func find(_ name: String) -> (any Benchmark)? {
        self.all.first { $0.name == name }
    }

    /// The workload matrix in the JSON format that the workload equivalence check compares across clients.
    func workloadMatrix() -> [BenchmarkWorkload] {
        self.all.map { benchmark in
            BenchmarkWorkload(
                benchmark: benchmark.name,
                description: benchmark.description,
                latencyMetric: benchmark.latencyMetric.rawValue,
                operationUnit: benchmark.operationUnit.rawValue,
                cases: benchmark.cases.map { entry in
                    BenchmarkCaseEntry(name: entry.name, parameters: entry.parameters)
                }
            )
        }
    }
}

/// The outcome of one benchmark case within one client process.
enum TrialStatus: String, Sendable {
    case completed
    case failed
}

/// The measurement of one benchmark case in one client process.
///
/// `elapsedNanoseconds` covers only the measured phase. Channel creation, the readiness probe, and
/// warmup run before it and are never included.
///
/// A trial stops at the first failed operation. `operations` and the byte counts then hold the
/// work that completed before the failure, and `latencySamplesNanoseconds` holds its samples.
struct TrialResult: Sendable {
    let benchmarkName: String
    let caseName: String
    let parameters: BenchmarkParameters
    let latencyMetric: LatencyMetric
    let operationUnit: OperationUnit
    let injectedDelayMs: Int
    let status: TrialStatus
    let error: String?
    let elapsedNanoseconds: Int64
    let operations: Int64
    let requestMessages: Int64?
    let responseMessages: Int64?
    let applicationRequestBytes: Int64
    let applicationResponseBytes: Int64
    let latencySamplesNanoseconds: [Int64]
    let timeToFirstResponseNanoseconds: [Int64]
    let streamDurationNanoseconds: [Int64]
    let finalResponseLatencyNanoseconds: [Int64]
    /// The CPU time of the measured phase, from resource snapshots at its boundaries.
    /// `maxRssBytes` is the process-lifetime peak at the end of the trial, warmup included.
    var cpuUserNanoseconds: Int64? = nil
    var cpuSystemNanoseconds: Int64? = nil
    var cpuTotalNanoseconds: Int64? = nil
    var maxRssBytes: Int64? = nil
}

/// A `TrialResult` with its process context: implementation identity, platform label, server
/// target, wall-clock start time, and the measured connection readiness of the channel.
struct TrialRecord: Encodable, Sendable {
    let implementationName: String
    let implementationVersion: String?
    let platform: String
    let target: String
    let startedAtEpochMs: Int64
    let connectionReadyNanoseconds: Int64?
    let trial: TrialResult

    enum CodingKeys: String, CodingKey {
        case schemaVersion = "schema_version"
        case implementation
        case implementationVersion = "implementation_version"
        case platform
        case benchmark
        case benchmarkCase = "case"
        case parameters
        case latencyMetric = "latency_metric"
        case operationUnit = "operation_unit"
        case loadShape = "load_shape"
        case target
        case injectedDelayMs = "injected_delay_ms"
        case status
        case error
        case startedAtEpochMs = "started_at_epoch_ms"
        case connectionReadyNanoseconds = "connection_ready_ns"
        case elapsedNanoseconds = "elapsed_ns"
        case counts
        case latencySamplesNanoseconds = "latency_samples_ns"
        case events
        case cpuUserNanoseconds = "cpu_user_ns"
        case cpuSystemNanoseconds = "cpu_system_ns"
        case cpuTotalNanoseconds = "cpu_total_ns"
        case maxRssBytes = "max_rss_bytes"
    }

    struct Counts: Encodable, Sendable {
        let operations: Int64
        let requestMessages: Int64?
        let responseMessages: Int64?

        enum CodingKeys: String, CodingKey {
            case operations
            case requestMessages = "request_messages"
            case responseMessages = "response_messages"
            case applicationRequestBytes = "application_request_bytes"
            case applicationResponseBytes = "application_response_bytes"
        }

        let applicationRequestBytes: Int64
        let applicationResponseBytes: Int64
    }

    struct Events: Encodable, Sendable {
        let timeToFirstResponseNanoseconds: [Int64]
        let streamDurationNanoseconds: [Int64]
        let finalResponseLatencyNanoseconds: [Int64]

        enum CodingKeys: String, CodingKey {
            case timeToFirstResponseNanoseconds = "time_to_first_response_ns"
            case streamDurationNanoseconds = "stream_duration_ns"
            case finalResponseLatencyNanoseconds = "final_response_latency_ns"
        }
    }

    func encode(to encoder: Encoder) throws {
        let trial = self.trial
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(trialSchemaVersion, forKey: .schemaVersion)
        try container.encode(self.implementationName, forKey: .implementation)
        // The Kotlin clients write an explicit null, so the Swift record must
        // keep the key present with the same value.
        if let implementationVersion = self.implementationVersion {
            try container.encode(implementationVersion, forKey: .implementationVersion)
        } else {
            try container.encodeNil(forKey: .implementationVersion)
        }
        try container.encode(self.platform, forKey: .platform)
        try container.encode(trial.benchmarkName, forKey: .benchmark)
        try container.encode(trial.caseName, forKey: .benchmarkCase)
        try container.encode(trial.parameters, forKey: .parameters)
        try container.encode(trial.latencyMetric.rawValue, forKey: .latencyMetric)
        try container.encode(trial.operationUnit.rawValue, forKey: .operationUnit)
        try container.encode(loadShape, forKey: .loadShape)
        try container.encode(self.target, forKey: .target)
        try container.encode(trial.injectedDelayMs, forKey: .injectedDelayMs)
        try container.encode(trial.status.rawValue, forKey: .status)
        try container.encodeIfPresent(trial.error, forKey: .error)
        try container.encode(self.startedAtEpochMs, forKey: .startedAtEpochMs)
        try container.encodeIfPresent(self.connectionReadyNanoseconds, forKey: .connectionReadyNanoseconds)
        try container.encode(trial.elapsedNanoseconds, forKey: .elapsedNanoseconds)
        try container.encode(
            Counts(
                operations: trial.operations,
                requestMessages: trial.requestMessages,
                responseMessages: trial.responseMessages,
                applicationRequestBytes: trial.applicationRequestBytes,
                applicationResponseBytes: trial.applicationResponseBytes
            ),
            forKey: .counts
        )
        try container.encode(trial.latencySamplesNanoseconds, forKey: .latencySamplesNanoseconds)
        try container.encode(
            Events(
                timeToFirstResponseNanoseconds: trial.timeToFirstResponseNanoseconds,
                streamDurationNanoseconds: trial.streamDurationNanoseconds,
                finalResponseLatencyNanoseconds: trial.finalResponseLatencyNanoseconds
            ),
            forKey: .events
        )
        // The Kotlin clients write explicit nulls for absent counters, so the Swift record
        // keeps the same keys and values.
        try encodeOptional(trial.cpuUserNanoseconds, to: container, for: .cpuUserNanoseconds)
        try encodeOptional(trial.cpuSystemNanoseconds, to: container, for: .cpuSystemNanoseconds)
        try encodeOptional(trial.cpuTotalNanoseconds, to: container, for: .cpuTotalNanoseconds)
        try encodeOptional(trial.maxRssBytes, to: container, for: .maxRssBytes)
    }
}

private func encodeOptional<N: Encodable>(
    _ value: N?,
    to container: KeyedEncodingContainer<TrialRecord.CodingKeys>,
    for key: TrialRecord.CodingKeys
) throws {
    // The encoding requirements of KeyedEncodingContainerProtocol are mutating,
    // so this works on a mutable copy of the passed container.
    var container = container
    if let value {
        try container.encode(value, forKey: key)
    } else {
        try container.encodeNil(forKey: key)
    }
}

struct LatencyStatistics: Equatable, Sendable {
    let sampleCount: Int
    let minimumNanoseconds: Int64?
    let meanNanoseconds: Int64?
    let p50Nanoseconds: Int64?
    let p90Nanoseconds: Int64?
    let p95Nanoseconds: Int64?
    let p99Nanoseconds: Int64?
    let p999Nanoseconds: Int64?
    let maximumNanoseconds: Int64?

    /// Computes the summary with the nearest-rank percentile method.
    ///
    /// Returns a summary with null values when there are no samples, which happens when a stream
    /// delivers fewer than two messages.
    init(samples: [Int64]) {
        guard !samples.isEmpty else {
            self.sampleCount = 0
            self.minimumNanoseconds = nil
            self.meanNanoseconds = nil
            self.p50Nanoseconds = nil
            self.p90Nanoseconds = nil
            self.p95Nanoseconds = nil
            self.p99Nanoseconds = nil
            self.p999Nanoseconds = nil
            self.maximumNanoseconds = nil
            return
        }
        let sorted = samples.sorted()

        func percentile(_ value: Double) -> Int64 {
            let index = Int((value * Double(sorted.count - 1)).rounded())
            return sorted[index]
        }

        self.sampleCount = sorted.count
        self.minimumNanoseconds = sorted.first
        self.meanNanoseconds = Int64(
            (samples.reduce(0.0) { $0 + Double($1) } / Double(samples.count)).rounded(.towardZero)
        )
        self.p50Nanoseconds = percentile(0.50)
        self.p90Nanoseconds = percentile(0.90)
        self.p95Nanoseconds = percentile(0.95)
        self.p99Nanoseconds = percentile(0.99)
        self.p999Nanoseconds = percentile(0.999)
        self.maximumNanoseconds = sorted.last
    }
}

/// Calls the server until one call succeeds, and returns the time from the first call to that
/// success. The first call pays for the connection setup, so the returned duration is an upper
/// bound for the connect time of the channel.
///
/// A call that fails because the server isn't listening yet is retried after a short delay.
func awaitReadiness(
    client: BenchmarkClient,
    target: String,
    timeout: Duration
) async throws -> Duration {
    let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
    var payload = Grpc_Testing_Payload()
    payload.type = .compressable
    var request = Grpc_Testing_SimpleRequest()
    request.responseType = .compressable
    request.responseSize = 0
    request.payload = payload

    let clock = ContinuousClock()
    let started = clock.now
    var lastError: String?

    while started.duration(to: clock.now) < timeout {
        do {
            _ = try await service.unaryCall(request)
            return started.duration(to: clock.now)
        } catch {
            if error is CancellationError { throw error }
            lastError = String(describing: error)
            try await Task.sleep(nanoseconds: 250_000_000)
        }
    }
    throw ReadinessTimeoutError(target: target, timeout: timeout, lastError: lastError)
}

struct ReadinessTimeoutError: Error, CustomStringConvertible, Sendable {
    let target: String
    let timeout: Duration
    let lastError: String?

    var description: String {
        let lastError = self.lastError.map { " Last error: \($0)" } ?? ""
        return
            "The benchmark server at '\(self.target)' did not accept a readiness call within \(self.timeout).\(lastError)"
    }
}

extension Duration {
    var secondsValue: Double {
        let components = self.components
        return Double(components.seconds) + Double(components.attoseconds) / 1_000_000_000_000_000_000
    }

    var millisecondsValue: Int64 {
        Int64((self.secondsValue * 1_000).rounded())
    }
}

func monotonicNanoseconds() -> UInt64 {
    DispatchTime.now().uptimeNanoseconds
}

let currentPlatform: String = {
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
