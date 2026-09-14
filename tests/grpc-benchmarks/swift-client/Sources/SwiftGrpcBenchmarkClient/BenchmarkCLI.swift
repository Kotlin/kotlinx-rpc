/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCNIOTransportHTTP2TransportServices

enum OutputFormat: String, Sendable {
    case human
    case csv
}

enum ParsedCommand: Equatable, Sendable {
    case help(String?)
    case list
    case run(RunConfiguration)
}

struct RunConfiguration: Equatable, Sendable {
    let benchmarkName: String
    let target: ServerTarget
    let overrides: BenchmarkOverrides
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

    init(registry: BenchmarkRegistry = BenchmarkRegistry(unaryBenchmarks())) {
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
            guard arguments.count == 1 else { throw CLIError.usage("list does not accept arguments") }
            return .list
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
        case .list:
            print(self.listBenchmarks())
        case .run(let configuration):
            try await self.run(configuration)
        }
    }

    func help(for command: String?) throws -> String {
        switch command {
        case nil:
            return Self.rootHelp
        case "help":
            return "Usage: swift-grpc-benchmark-client help [COMMAND]\n\nShow help for the CLI or a command."
        case "list":
            return "Usage: swift-grpc-benchmark-client list\n\nList the available benchmarks."
        case "run":
            return Self.runHelp
        case .some(let name):
            throw CLIError.usage("Unknown command '\(name)'")
        }
    }

    func listBenchmarks() -> String {
        let entries = self.registry.all.map { benchmark in
            "  \(benchmark.name.padding(toLength: 20, withPad: " ", startingAt: 0)) \(benchmark.description)"
        }
        return (["Available benchmarks:"] + entries).joined(separator: "\n")
    }

    private func parseRun(_ arguments: [String]) throws -> ParsedCommand {
        if arguments == ["-h"] || arguments == ["--help"] {
            return .help("run")
        }
        guard let benchmarkName = arguments.first, !benchmarkName.hasPrefix("-") else {
            throw CLIError.usage("run requires a benchmark name or 'all'")
        }
        guard benchmarkName == "all" || self.registry.find(benchmarkName) != nil else {
            throw CLIError.usage("'\(benchmarkName)' is not a registered benchmark")
        }

        var target = try ServerTarget.parse("localhost:50051")
        var overrides = BenchmarkOverrides()
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
            case "--warmup":
                overrides.warmupCalls = try parseInteger(value, option: option)
            case "--calls":
                overrides.calls = try parseInteger(value, option: option)
            case "--concurrency":
                overrides.concurrency = try parseInteger(value, option: option)
            case "--request-bytes":
                overrides.requestBytes = try parseInteger(value, option: option)
            case "--response-bytes":
                overrides.responseBytes = try parseInteger(value, option: option)
            case "--format":
                guard let parsed = OutputFormat(rawValue: value) else {
                    throw CLIError.usage("Invalid --format '\(value)'; expected human or csv")
                }
                format = parsed
            default:
                throw CLIError.usage("Unknown option '\(option)'")
            }
            index += 2
        }

        let configuration = RunConfiguration(
            benchmarkName: benchmarkName,
            target: target,
            overrides: overrides,
            format: format
        )
        for benchmark in self.selectedBenchmarks(configuration) {
            do {
                _ = try overrides.applying(to: benchmark.defaults)
            } catch {
                throw CLIError.usage(String(describing: error))
            }
        }
        return .run(configuration)
    }

    private func run(_ configuration: RunConfiguration) async throws {
        let selected = self.selectedBenchmarks(configuration)
        let target = configuration.target

        do {
            let transport = try HTTP2ClientTransport.TransportServices(
                target: .dns(host: target.host, port: target.port),
                transportSecurity: .plaintext
            )
            let results = try await withGRPCClient(transport: transport) { client in
                var results: [BenchmarkResult] = []
                for benchmark in selected {
                    let parameters = try configuration.overrides.applying(to: benchmark.defaults)
                    results.append(try await benchmark.run(client: client, parameters: parameters))
                }
                return results
            }
            print(render(results: results, target: target, format: configuration.format))
        } catch {
            throw CLIError.benchmark("Benchmark failed: \(error)")
        }
    }

    private func selectedBenchmarks(_ configuration: RunConfiguration) -> [any Benchmark] {
        if configuration.benchmarkName == "all" {
            return self.registry.all
        }
        return [self.registry.find(configuration.benchmarkName)!]
    }

    private static let rootHelp = """
        Usage: swift-grpc-benchmark-client <command>

        Run gRPC Swift client benchmarks against a benchmark server.

        Commands:
          help [command]  Show help for the CLI or a command
          list            List the available benchmarks
          run             Run one benchmark, or all registered benchmarks
        """

    private static let runHelp = """
        Usage: swift-grpc-benchmark-client run <benchmark|all> [options]

        Options:
          --target HOST:PORT       Benchmark server (default: localhost:50051)
          --warmup COUNT           Warmup calls before measurement
          --calls COUNT            Measured calls
          --concurrency COUNT      Concurrent workers
          --request-bytes COUNT    Request payload bytes
          --response-bytes COUNT   Response payload bytes
          --format human|csv       Output format (default: human)
        """
}

private func parseInteger(_ value: String, option: String) throws -> Int {
    guard let result = Int(value) else {
        throw CLIError.usage("Invalid value '\(value)' for \(option); expected an integer")
    }
    return result
}

func render(results: [BenchmarkResult], target: ServerTarget, format: OutputFormat) -> String {
    switch format {
    case .human:
        return results.map { $0.renderHuman(target: target) }.joined(separator: "\n\n")
    case .csv:
        return ([csvHeader] + results.map { $0.renderCSV(target: target) }).joined(separator: "\n")
    }
}

private let csvHeader =
    "benchmark,platform,target,warmup_calls,calls,concurrency,request_bytes,response_bytes,elapsed_seconds," +
    "calls_per_second,application_bytes_per_second,latency_min_us,latency_mean_us,latency_p50_us," +
    "latency_p90_us,latency_p95_us,latency_p99_us,latency_p999_us,latency_max_us"

private extension BenchmarkResult {
    func renderHuman(target: ServerTarget) -> String {
        """
        benchmark: \(self.benchmarkName)
        platform: \(self.platform)
        target: \(target)
        calls: \(self.parameters.calls) (\(self.parameters.warmupCalls) warmup)
        concurrency: \(self.parameters.concurrency)
        payload: \(self.parameters.requestBytes) B request / \(self.parameters.responseBytes) B response
        elapsed: \(self.elapsed.seconds.formatted(6)) s
        throughput: \(self.callsPerSecond.formatted(2)) calls/s, \(self.applicationBytesPerSecond.bytesPerSecond)
        latency (us):
          min=\(self.latency.minimumNanoseconds.microseconds) mean=\(self.latency.meanNanoseconds.microseconds) p50=\(self.latency.p50Nanoseconds.microseconds)
          p90=\(self.latency.p90Nanoseconds.microseconds) p95=\(self.latency.p95Nanoseconds.microseconds) p99=\(self.latency.p99Nanoseconds.microseconds)
          p99.9=\(self.latency.p999Nanoseconds.microseconds) max=\(self.latency.maximumNanoseconds.microseconds)
        """
    }

    func renderCSV(target: ServerTarget) -> String {
        [
            self.benchmarkName,
            self.platform,
            target.description,
            String(self.parameters.warmupCalls),
            String(self.parameters.calls),
            String(self.parameters.concurrency),
            String(self.parameters.requestBytes),
            String(self.parameters.responseBytes),
            self.elapsed.seconds.formatted(6),
            self.callsPerSecond.formatted(4),
            self.applicationBytesPerSecond.formatted(4),
            self.latency.minimumNanoseconds.microseconds,
            self.latency.meanNanoseconds.microseconds,
            self.latency.p50Nanoseconds.microseconds,
            self.latency.p90Nanoseconds.microseconds,
            self.latency.p95Nanoseconds.microseconds,
            self.latency.p99Nanoseconds.microseconds,
            self.latency.p999Nanoseconds.microseconds,
            self.latency.maximumNanoseconds.microseconds,
        ].joined(separator: ",")
    }
}

private extension UInt64 {
    var microseconds: String {
        (Double(self) / 1_000).formatted(3)
    }
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
