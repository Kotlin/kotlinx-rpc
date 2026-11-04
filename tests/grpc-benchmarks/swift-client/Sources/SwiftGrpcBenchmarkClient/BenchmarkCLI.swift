/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCNIOTransportHTTP2TransportServices

enum OutputFormat: String, Sendable {
    case human
    case csv
    case json
}

enum ParsedCommand: Equatable {
    case help(String?)
    case list(format: OutputFormat)
    case ping(target: ServerTarget, format: String, timeoutSeconds: Int)
    case run(RunConfiguration)
}

struct RunConfiguration: Equatable, Sendable {
    let benchmarkName: String
    let caseName: String?
    let target: ServerTarget
    let overrides: BenchmarkOverrides
    let injectedDelayMs: Int
    let platformLabel: String?
    let readinessTimeoutSeconds: Int
    let format: OutputFormat
}

struct CLIError: Error, Equatable, CustomStringConvertible, Sendable {
    let description: String
    let exitCode: Int32

    static func usage(_ description: String) -> CLIError {
        CLIError(description: description, exitCode: 2)
    }

    static func benchmark(_ description: String) -> CLIError {
        CLIError(description: description, exitCode: 1)
    }
}

struct BenchmarkCLI: Sendable {
    let registry: BenchmarkRegistry

    init(registry: BenchmarkRegistry = BenchmarkRegistry(unaryBenchmarks() + streamingBenchmarks())) {
        self.registry = registry
    }

    func parse(arguments: [String]) throws -> ParsedCommand {
        guard let command = arguments.first else { return .help(nil) }

        switch command {
        case "-h", "--help":
            return .help(nil)
        case "help":
            guard arguments.count <= 2 else { throw CLIError.usage("help accepts at most one command") }
            return .help(arguments.count == 2 ? arguments[1] : nil)
        case "list":
            return try self.parseList(Array(arguments.dropFirst()))
        case "ping":
            return try self.parsePing(Array(arguments.dropFirst()))
        case "run":
            return try self.parseRun(Array(arguments.dropFirst()))
        default:
            throw CLIError.usage("Unknown command '\(command)'")
        }
    }

    func execute(arguments: [String]) async throws {
        switch try self.parse(arguments: arguments) {
        case .help(let command):
            print(try self.help(for: command))
        case .list(let format):
            switch format {
            case .human, .csv:
                print(self.listBenchmarks())
            case .json:
                let encoder = JSONEncoder()
                encoder.outputFormatting = [.prettyPrinted, .sortedKeys]
                let data = try encoder.encode(self.registry.workloadMatrix())
                print(String(data: data, encoding: .utf8)!)
            }
        case .ping(let target, let format, let timeoutSeconds):
            try await self.ping(target: target, format: format, timeoutSeconds: timeoutSeconds)
        case .run(let configuration):
            try await self.run(configuration)
        }
    }

    private func parseList(_ arguments: [String]) throws -> ParsedCommand {
        var format = OutputFormat.human
        var index = 0
        while index < arguments.count {
            let option = arguments[index]
            guard index + 1 < arguments.count else {
                throw CLIError.usage("Missing value for \(option)")
            }
            let value = arguments[index + 1]
            switch option {
            case "--format":
                guard let parsed = OutputFormat(rawValue: value) else {
                    throw CLIError.usage("Invalid --format '\(value)'; expected human, csv, or json")
                }
                format = parsed
            default:
                throw CLIError.usage("Unknown option '\(option)'")
            }
            index += 2
        }
        return .list(format: format)
    }

    private func parsePing(_ arguments: [String]) throws -> ParsedCommand {
        var target = try ServerTarget.parse("localhost:50051")
        var format = "human"
        var timeoutSeconds = 30
        var index = 0
        while index < arguments.count {
            let option = arguments[index]
            guard index + 1 < arguments.count else {
                throw CLIError.usage("Missing value for \(option)")
            }
            let value = arguments[index + 1]
            switch option {
            case "--target":
                target = try ServerTarget.parse(value)
            case "--format":
                guard value == "human" || value == "json" else {
                    throw CLIError.usage("Invalid --format '\(value)'; expected human or json")
                }
                format = value
            case "--timeout":
                timeoutSeconds = try parsePositiveInteger(value, option: option)
            default:
                throw CLIError.usage("Unknown option '\(option)'")
            }
            index += 2
        }
        return .ping(target: target, format: format, timeoutSeconds: timeoutSeconds)
    }

    private func parseRun(_ arguments: [String]) throws -> ParsedCommand {
        guard let benchmarkName = arguments.first, !benchmarkName.hasPrefix("-") else {
            throw CLIError.usage("Missing benchmark name. Pass 'all' or a name from 'list'.")
        }
        guard benchmarkName == "all" || self.registry.find(benchmarkName) != nil else {
            throw CLIError.usage("Unknown benchmark '\(benchmarkName)'. Run 'list' to see the registered benchmarks.")
        }

        var target = try ServerTarget.parse("localhost:50051")
        var caseName: String?
        var overrides = BenchmarkOverrides()
        var injectedDelayMs = 0
        var platformLabel: String?
        var readinessTimeoutSeconds = 30
        var format = OutputFormat.human
        var index = 1

        while index < arguments.count {
            let option = arguments[index]
            guard index + 1 < arguments.count else {
                throw CLIError.usage("Missing value for \(option)")
            }
            let value = arguments[index + 1]

            switch option {
            case "--target":
                target = try ServerTarget.parse(value)
            case "--case":
                caseName = value
            case "--warmup":
                overrides.warmupOperations = try parseNonNegativeInteger(value, option: option)
            case "--operations":
                overrides.operations = try parsePositiveInteger(value, option: option)
            case "--concurrency":
                overrides.concurrency = try parsePositiveInteger(value, option: option)
            case "--request-bytes":
                overrides.requestBytes = try parseNonNegativeInteger(value, option: option)
            case "--response-bytes":
                overrides.responseBytes = try parseNonNegativeInteger(value, option: option)
            case "--inject-delay-ms":
                injectedDelayMs = try parseNonNegativeInteger(value, option: option)
            case "--platform-label":
                platformLabel = value
            case "--readiness-timeout":
                readinessTimeoutSeconds = try parsePositiveInteger(value, option: option)
            case "--format":
                guard let parsed = OutputFormat(rawValue: value) else {
                    throw CLIError.usage("Invalid --format '\(value)'; expected human, csv, or json")
                }
                format = parsed
            default:
                throw CLIError.usage("Unknown option '\(option)'")
            }
            index += 2
        }

        if caseName != nil && benchmarkName == "all" {
            throw CLIError.usage("--case requires a specific benchmark name")
        }
        let configuration = RunConfiguration(
            benchmarkName: benchmarkName,
            caseName: caseName,
            target: target,
            overrides: overrides,
            injectedDelayMs: injectedDelayMs,
            platformLabel: platformLabel,
            readinessTimeoutSeconds: readinessTimeoutSeconds,
            format: format
        )
        for benchmark in self.selectedBenchmarks(configuration) {
            for benchmarkCase in try self.selectedCases(benchmark, named: caseName) {
                do {
                    _ = try configuration.overrides.applying(to: benchmarkCase.parameters)
                } catch {
                    throw CLIError.usage(String(describing: error))
                }
            }
        }
        return .run(configuration)
    }

    private func ping(target: ServerTarget, format: String, timeoutSeconds: Int) async throws {
        let transport = try HTTP2ClientTransport.TransportServices(
            target: .dns(host: target.host, port: target.port),
            transportSecurity: .plaintext
        )

        let ready: Duration
        do {
            ready = try await withGRPCClient(transport: transport) { client in
                try await awaitReadiness(
                    client: client,
                    target: target.description,
                    timeout: .seconds(timeoutSeconds)
                )
            }
        } catch {
            if format == "json" {
                print("{\"target\":\"\(target)\",\"ready\":false}")
            } else {
                writeError(String(describing: error))
            }
            throw CLIError.benchmark(String(describing: error))
        }

        if format == "json" {
            print("{\"target\":\"\(target)\",\"ready\":true,\"connection_ready_ns\":\(ready.nanosecondsValue)}")
        } else {
            print("The server at '\(target)' accepted a readiness call after \(ready.millisecondsValue) ms.")
        }
    }

    private func run(_ configuration: RunConfiguration) async throws {
        let selected = self.selectedBenchmarks(configuration)
        let target = configuration.target
        let benchmarkCases = try selected.flatMap { benchmark in
            try self.selectedCases(benchmark, named: configuration.caseName).map { (benchmark, $0) }
        }
        let encoder = JSONEncoder()

        var records: [TrialRecord] = []
        var progressLineLength = 0
        do {
            for (index, benchmarkAndCase) in benchmarkCases.enumerated() {
                let (benchmark, benchmarkCase) = benchmarkAndCase
                if benchmarkCases.count > 1 {
                    let progress = renderBenchmarkProgress(
                        completed: index,
                        total: benchmarkCases.count,
                        platform: configuration.platformLabel ?? currentPlatform,
                        implementation: "swift",
                        benchmark: benchmark.name,
                        benchmarkCase: benchmarkCase.name
                    )
                    writeBenchmarkProgress(
                        progress: progress,
                        previousLength: progressLineLength,
                        finished: false
                    )
                    progressLineLength = progress.count
                }
                records.append(
                    try await self.runTrial(
                        benchmark,
                        benchmarkCase: benchmarkCase,
                        configuration: configuration
                    )
                )
            }
            if benchmarkCases.count > 1 {
                let progress = renderBenchmarkProgress(
                    completed: benchmarkCases.count,
                    total: benchmarkCases.count,
                    platform: configuration.platformLabel ?? currentPlatform,
                    implementation: "swift"
                )
                writeBenchmarkProgress(
                    progress: progress,
                    previousLength: progressLineLength,
                    finished: true
                )
            }
        } catch {
            writeBenchmarkProgress(
                progress: renderBenchmarkProgressLineBreak(previousLength: progressLineLength),
                previousLength: 0,
                finished: true
            )
            throw error
        }

        switch configuration.format {
        case .human:
            print(records.map { renderHuman(record: $0) }.joined(separator: "\n\n"))
        case .csv:
            print(([csvHeader] + records.map { renderCSV(record: $0) }).joined(separator: "\n"))
        case .json:
            // One JSON object per line, so a run over many cases writes one record per line.
            for record in records {
                let data = try encoder.encode(record)
                print(String(data: data, encoding: .utf8)!)
            }
        }
        if records.contains(where: { $0.trial.status == .failed }) {
            throw CLIError.benchmark("At least one benchmark case failed. The records report the status of each case.")
        }
    }

    /// Runs one case on its own channel and returns its record. The record keeps a failed status
    /// when readiness or the case itself fails, so the remaining cases still run.
    private func runTrial(
        _ benchmark: any Benchmark,
        benchmarkCase: BenchmarkCase,
        configuration: RunConfiguration
    ) async throws -> TrialRecord {
        let startedAtEpochMs = Int64(Date().timeIntervalSince1970 * 1000)
        let transport = try HTTP2ClientTransport.TransportServices(
            target: .dns(host: configuration.target.host, port: configuration.target.port),
            transportSecurity: .plaintext
        )

        return try await withGRPCClient(transport: transport) { client in
            let parameters = try configuration.overrides.applying(to: benchmarkCase.parameters)
            do {
                let connectionReady = try await awaitReadiness(
                    client: client,
                    target: configuration.target.description,
                    timeout: .seconds(configuration.readinessTimeoutSeconds)
                )
                let trial = try await benchmark.run(
                    client: client,
                    benchmarkCase: benchmarkCase,
                    parameters: parameters,
                    injectedDelayMs: configuration.injectedDelayMs
                )
                return TrialRecord(
                    implementationName: "swift",
                    implementationVersion: nil,
                    platform: configuration.platformLabel ?? currentPlatform,
                    target: configuration.target.description,
                    startedAtEpochMs: startedAtEpochMs,
                    connectionReadyNanoseconds: connectionReady.nanosecondsValue,
                    trial: trial
                )
            } catch {
                if error is CancellationError { throw error }
                return TrialRecord(
                    implementationName: "swift",
                    implementationVersion: nil,
                    platform: configuration.platformLabel ?? currentPlatform,
                    target: configuration.target.description,
                    startedAtEpochMs: startedAtEpochMs,
                    connectionReadyNanoseconds: nil,
                    trial: TrialResult(
                        benchmarkName: benchmark.name,
                        caseName: benchmarkCase.name,
                        parameters: parameters,
                        latencyMetric: benchmark.latencyMetric,
                        operationUnit: benchmark.operationUnit,
                        injectedDelayMs: configuration.injectedDelayMs,
                        status: .failed,
                        error: String(describing: error),
                        elapsedNanoseconds: 0,
                        operations: 0,
                        requestMessages: benchmark.operationUnit == .message ? 0 : nil,
                        responseMessages: benchmark.operationUnit == .message ? 0 : nil,
                        applicationRequestBytes: 0,
                        applicationResponseBytes: 0,
                        latencySamplesNanoseconds: [],
                        timeToFirstResponseNanoseconds: [],
                        streamDurationNanoseconds: [],
                        finalResponseLatencyNanoseconds: []
                    )
                )
            }
        }
    }

    private func selectedBenchmarks(_ configuration: RunConfiguration) -> [any Benchmark] {
        if configuration.benchmarkName == "all" {
            return self.registry.all
        }
        return [self.registry.find(configuration.benchmarkName)!]
    }

    private func selectedCases(_ benchmark: any Benchmark, named caseName: String?) throws -> [BenchmarkCase] {
        guard let caseName else { return benchmark.cases }
        guard let benchmarkCase = benchmark.cases.first(where: { $0.name == caseName }) else {
            throw CLIError.usage("Benchmark '\(benchmark.name)' has no case '\(caseName)'")
        }
        return [benchmarkCase]
    }

    func listBenchmarks() -> String {
        var lines = ["Available benchmarks:"]
        for benchmark in self.registry.all {
            let cases = benchmark.cases.count == 1 ? "" : " (\(benchmark.cases.count) cases)"
            lines.append("  \(benchmark.name.padding(toLength: 28, withPad: " ", startingAt: 0)) \(benchmark.description)\(cases)")
            for benchmarkCase in benchmark.cases {
                let parameters = benchmarkCase.parameters
                lines.append(
                    "    \(benchmarkCase.name.padding(toLength: 18, withPad: " ", startingAt: 0)) " +
                        "warmup=\(parameters.warmupOperations), operations=\(parameters.operations), " +
                        "concurrency=\(parameters.concurrency), " +
                        "request=\(parameters.requestBytes) B, response=\(parameters.responseBytes) B"
                )
            }
        }
        return lines.joined(separator: "\n")
    }

    private static let rootHelp = """
        Usage: swift-grpc-benchmark-client <command>

        Run gRPC Swift client benchmarks against a benchmark server.

        Commands:
          help [command]  Show help for the CLI or a command
          list            List the available benchmarks
          ping            Check the server readiness and the connection time
          run             Run one benchmark, or all registered benchmarks
        """

    private static let runHelp = """
        Usage: swift-grpc-benchmark-client run <benchmark|all> [options]

        Options:
          --target HOST:PORT       Benchmark server (default: localhost:50051)
          --case NAME              Run one named benchmark case
          --warmup COUNT           Warmup operations before measurement
          --operations COUNT       Measured operations
          --concurrency COUNT      Concurrent workers or streams
          --request-bytes COUNT    Request payload bytes
          --response-bytes COUNT   Response payload bytes
          --inject-delay-ms COUNT  Test hook: adds this delay to every measured operation
          --platform-label NAME    Platform name written to the results
          --readiness-timeout SEC Seconds to wait for the server readiness probe (default: 30)
          --format human|csv|json  Output format (default: human)
        """

    private static let pingHelp = """
        Usage: swift-grpc-benchmark-client ping [options]

        Options:
          --target HOST:PORT  Benchmark server (default: localhost:50051)
          --timeout SECONDS   Readiness timeout (default: 30)
          --format human|json Output format (default: human)
        """

    func help(for command: String?) throws -> String {
        switch command {
        case nil, "help":
            return Self.rootHelp
        case "list":
            return "Usage: swift-grpc-benchmark-client list [--format human|json]"
        case "ping":
            return Self.pingHelp
        case "run":
            return Self.runHelp
        case let name:
            throw CLIError.usage("Unknown command '\(name)'")
        }
    }
}

private func parseNonNegativeInteger(_ value: String, option: String) throws -> Int {
    let result = try parseInteger(value, option: option)
    guard result >= 0 else {
        throw CLIError.usage("Invalid value '\(value)' for \(option); must be non-negative")
    }
    return result
}

private func parsePositiveInteger(_ value: String, option: String) throws -> Int {
    let result = try parseInteger(value, option: option)
    guard result > 0 else {
        throw CLIError.usage("Invalid value '\(value)' for \(option); must be positive")
    }
    return result
}

private func parseInteger(_ value: String, option: String) throws -> Int {
    guard let result = Int(value) else {
        throw CLIError.usage("Invalid value '\(value)' for \(option); expected an integer")
    }
    return result
}

private let progressBarWidth = 20

func renderBenchmarkProgressUpdate(progress: String, previousLength: Int, finished: Bool) -> String {
    let overwrite = previousLength > 0 ? renderBenchmarkProgressClear(length: previousLength) : ""
    return overwrite + progress + (finished ? "\n" : "")
}

func renderBenchmarkProgressLineBreak(previousLength: Int) -> String {
    previousLength > 0 ? "\n" : ""
}

private func renderBenchmarkProgressClear(length: Int) -> String {
    "\r" + String(repeating: " ", count: length) + "\r"
}

func renderBenchmarkProgress(
    completed: Int,
    total: Int,
    platform: String,
    implementation: String,
    benchmark: String? = nil,
    benchmarkCase: String? = nil
) -> String {
    precondition(total > 0, "Progress total must be positive")
    precondition((0...total).contains(completed), "Completed benchmark count must be between 0 and total")
    precondition(
        (benchmark == nil) == (benchmarkCase == nil),
        "Benchmark and case must either both be specified or both be omitted"
    )

    let filled = progressBarWidth * completed / total
    let bar: String
    if completed < total {
        bar = String(repeating: "=", count: filled) + ">" +
            String(repeating: "-", count: progressBarWidth - filled - 1)
    } else {
        bar = String(repeating: "=", count: progressBarWidth)
    }
    let activity: String
    if let benchmark, let benchmarkCase {
        activity = "running benchmark=\(benchmark) case=\(benchmarkCase)"
    } else {
        activity = "finished"
    }
    return "[\(bar)] \(completed)/\(total) complete, \(total - completed) remaining | " +
        "platform=\(platform) implementation=\(implementation) | \(activity)"
}

private func writeBenchmarkProgress(progress: String, previousLength: Int, finished: Bool) {
    writeStandardErrorWithoutNewline(
        renderBenchmarkProgressUpdate(
            progress: progress,
            previousLength: previousLength,
            finished: finished
        )
    )
}

private func writeStandardErrorWithoutNewline(_ message: String) {
    FileHandle.standardError.write(Data(message.utf8))
}

private func writeError(_ message: String) {
    FileHandle.standardError.write(Data("\(message)\n".utf8))
}

let csvHeader =
    "schema_version,implementation,platform,benchmark,case,status,latency_metric,operation_unit," +
    "injected_delay_ms,target,warmup_operations,operations,concurrency,request_bytes,response_bytes," +
    "elapsed_seconds,operations_per_second,request_messages,response_messages," +
    "request_messages_per_second,response_messages_per_second," +
    "application_request_bytes_per_second,application_response_bytes_per_second," +
    "latency_sample_count,latency_min_us,latency_mean_us,latency_p50_us,latency_p90_us," +
    "latency_p95_us,latency_p99_us,latency_p999_us,latency_max_us," +
    "time_to_first_response_mean_us,stream_duration_mean_us,final_response_latency_mean_us,error"

func renderCSV(record: TrialRecord) -> String {
    let trial = record.trial
    let statistics = LatencyStatistics(samples: trial.latencySamplesNanoseconds)
    let elapsedSeconds = Double(trial.elapsedNanoseconds) / 1_000_000_000.0
    let callsPerSecond: String
    if trial.operationUnit == .rpc && trial.status == .completed {
        callsPerSecond = (Double(trial.operations) / elapsedSeconds).formatted(4)
    } else {
        callsPerSecond = ""
    }

    return [
        String(trialSchemaVersion),
        csvField(record.implementationName),
        csvField(record.platform),
        csvField(trial.benchmarkName),
        csvField(trial.caseName),
        trial.status.rawValue,
        trial.latencyMetric.rawValue,
        trial.operationUnit.rawValue,
        String(trial.injectedDelayMs),
        csvField(record.target),
        String(trial.parameters.warmupOperations),
        String(trial.parameters.operations),
        String(trial.parameters.concurrency),
        String(trial.parameters.requestBytes),
        String(trial.parameters.responseBytes),
        elapsedSeconds.formatted(6),
        callsPerSecond,
        trial.requestMessages.map(String.init) ?? "",
        trial.responseMessages.map(String.init) ?? "",
        trial.requestMessages.map { (Double($0) / elapsedSeconds).formatted(4) } ?? "",
        trial.responseMessages.map { (Double($0) / elapsedSeconds).formatted(4) } ?? "",
        (Double(trial.applicationRequestBytes) / elapsedSeconds).formatted(4),
        (Double(trial.applicationResponseBytes) / elapsedSeconds).formatted(4),
        String(statistics.sampleCount),
        statistics.minimumNanoseconds.microseconds,
        statistics.meanNanoseconds.microseconds,
        statistics.p50Nanoseconds.microseconds,
        statistics.p90Nanoseconds.microseconds,
        statistics.p95Nanoseconds.microseconds,
        statistics.p99Nanoseconds.microseconds,
        statistics.p999Nanoseconds.microseconds,
        statistics.maximumNanoseconds.microseconds,
        trial.timeToFirstResponseNanoseconds.meanMicros,
        trial.streamDurationNanoseconds.meanMicros,
        trial.finalResponseLatencyNanoseconds.meanMicros,
        csvField(trial.error ?? ""),
    ].joined(separator: ",")
}

private func csvField(_ value: String) -> String {
    if value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r") {
        return "\"" + value.replacingOccurrences(of: "\"", with: "\"\"") + "\""
    }
    return value
}

private extension Array where Element == Int64 {
    var meanMicros: String {
        guard !self.isEmpty else { return "" }
        let mean = self.reduce(0.0) { $0 + Double($1) } / Double(self.count)
        return (mean / 1_000.0).formatted(3)
    }
}

private extension Optional where Wrapped == Int64 {
    var microseconds: String {
        switch self {
        case .none:
            return ""
        case .some(let value):
            return (Double(value) / 1_000.0).formatted(3)
        }
    }
}

func renderHuman(record: TrialRecord) -> String {
    let trial = record.trial
    let parameters = trial.parameters
    let statistics = LatencyStatistics(samples: trial.latencySamplesNanoseconds)
    let elapsedSeconds = Double(trial.elapsedNanoseconds) / 1_000_000_000.0
    var lines: [String] = []
    lines.append("benchmark: \(trial.benchmarkName)")
    lines.append("case: \(trial.caseName)")
    lines.append("implementation: \(record.implementationName)")
    lines.append("platform: \(record.platform)")
    lines.append("target: \(record.target)")
    lines.append("status: \(trial.status.rawValue)\(trial.error.map { " (\($0))" } ?? "")")
    lines.append("operations: \(parameters.operations) (\(parameters.warmupOperations) warmup) \(trial.operationUnit.rawValue)s")
    lines.append("concurrency: \(parameters.concurrency)")
    lines.append("payload: \(parameters.requestBytes) B request / \(parameters.responseBytes) B response")
    lines.append("injected delay: \(trial.injectedDelayMs) ms per operation")
    lines.append("elapsed: \(elapsedSeconds.formatted(6)) s")
    lines.append(
        "connection ready after: " +
            (record.connectionReadyNanoseconds.map { (Double($0) / 1_000_000.0).formatted(3) } ?? "not measured") +
            " ms"
    )
    if trial.operationUnit == .rpc {
        lines.append("throughput: \((Double(trial.operations) / elapsedSeconds).formatted(2)) calls/s")
    }
    if let request = trial.requestMessages, let response = trial.responseMessages {
        lines.append(
            "messages: \(request) request / \(response) response, " +
                "\((Double(request) / elapsedSeconds).formatted(2)) request/s, " +
                "\((Double(response) / elapsedSeconds).formatted(2)) response/s"
        )
    }
    if trial.applicationRequestBytes > 0 || trial.applicationResponseBytes > 0 {
        lines.append(
            "application data: " +
                "\((Double(trial.applicationRequestBytes) / elapsedSeconds).bytesPerSecond) request, " +
                "\((Double(trial.applicationResponseBytes) / elapsedSeconds).bytesPerSecond) response"
        )
    }
    lines.append("latency (\(trial.latencyMetric.rawValue), \(statistics.sampleCount) samples, us):")
    lines.append(
        "  min=\(statistics.minimumNanoseconds.microseconds) mean=\(statistics.meanNanoseconds.microseconds) " +
            "p50=\(statistics.p50Nanoseconds.microseconds)"
    )
    lines.append(
        "  p90=\(statistics.p90Nanoseconds.microseconds) p95=\(statistics.p95Nanoseconds.microseconds) " +
            "p99=\(statistics.p99Nanoseconds.microseconds)"
    )
    lines.append(
        "  p99.9=\(statistics.p999Nanoseconds.microseconds) max=\(statistics.maximumNanoseconds.microseconds)"
    )
    if !trial.timeToFirstResponseNanoseconds.isEmpty {
        lines.append(
            "time to first response (mean of \(trial.timeToFirstResponseNanoseconds.count) streams): " +
                trial.timeToFirstResponseNanoseconds.meanMicros + " us"
        )
    }
    if !trial.streamDurationNanoseconds.isEmpty {
        lines.append(
            "stream duration (mean of \(trial.streamDurationNanoseconds.count) streams): " +
                trial.streamDurationNanoseconds.meanMicros + " us"
        )
    }
    if !trial.finalResponseLatencyNanoseconds.isEmpty {
        lines.append(
            "final response latency (mean of \(trial.finalResponseLatencyNanoseconds.count) streams): " +
                trial.finalResponseLatencyNanoseconds.meanMicros + " us"
        )
    }
    return lines.joined(separator: "\n")
}

extension Double {
    var bytesPerSecond: String {
        switch self {
        case 1_000_000_000...:
            return "\((self / 1_000_000_000).formatted(2)) GB/s"
        case 1_000_000...:
            return "\((self / 1_000_000).formatted(2)) MB/s"
        case 1_000...:
            return "\((self / 1_000).formatted(2)) KB/s"
        default:
            return "\(self.formatted(2)) B/s"
        }
    }

    func formatted(_ decimalPlaces: Int) -> String {
        String(format: "%.*f", locale: Locale(identifier: "en_US_POSIX"), decimalPlaces, self)
    }
}
