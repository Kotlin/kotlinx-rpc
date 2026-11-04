/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore

/// Measurements produced by one completed streaming RPC.
struct StreamMeasurement: Sendable {
    let requestMessages: Int64
    let responseMessages: Int64
    let applicationRequestBytes: Int64
    let applicationResponseBytes: Int64
    let latencySamples: [Int64]
    let timeToFirstResponseNanoseconds: Int64?
    let streamDurationNanoseconds: Int64?
    let finalResponseLatencyNanoseconds: Int64?
}

/// Runs a streaming workload whose `operations` parameter is the number of messages per
/// direction, split across `concurrency` parallel streams on one channel.
struct StreamingBenchmark: Benchmark {
    let name: String
    let description: String
    let cases: [BenchmarkCase]
    let latencyMetric: LatencyMetric
    let prepare: @Sendable (BenchmarkClient) -> @Sendable (Int, BenchmarkParameters, Int) async throws -> StreamMeasurement

    var operationUnit: OperationUnit { .message }

    func run(
        client: BenchmarkClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int
    ) async throws -> TrialResult {
        let executeStream = self.prepare(client)

        do {
            _ = try await Self.executeStreams(
                messageCount: parameters.warmupOperations,
                parameters: parameters,
                injectedDelayMs: injectedDelayMs,
                executeStream: executeStream
            )
        } catch let failure as OperationFailure {
            return self.trialResult(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                .failed,
                "Warmup stream failed: \(failure)",
                elapsedNanoseconds: 0,
                completed: [],
                cpu: nil,
                resourcesAfter: nil
            )
        }

        let clock = ContinuousClock()
        let resourcesBefore = processResourceUsage()
        let started = clock.now
        let outcome = try await Self.executeStreams(
            messageCount: parameters.operations,
            parameters: parameters,
            injectedDelayMs: injectedDelayMs,
            executeStream: executeStream
        )
        let elapsed = started.duration(to: clock.now).nanosecondsValue
        let resourcesAfter = processResourceUsage()
        let cpu = cpuDelta(before: resourcesBefore, after: resourcesAfter)

        // A failed trial counts only the streams that completed. The messages of the stream
        // that failed, and of the streams cancelled with it, aren't counted.
        let completed = outcome.completed
        if let failure = outcome.failure {
            return self.trialResult(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                .failed,
                "Measured stream failed: \(failure)",
                elapsedNanoseconds: elapsed,
                completed: completed,
                cpu: cpu,
                resourcesAfter: resourcesAfter
            )
        }
        return self.trialResult(
            benchmarkCase,
            parameters,
            injectedDelayMs,
            .completed,
            nil,
            elapsedNanoseconds: elapsed,
            completed: completed,
            cpu: cpu,
            resourcesAfter: resourcesAfter
        )
    }

    private func trialResult(
        _ benchmarkCase: BenchmarkCase,
        _ parameters: BenchmarkParameters,
        _ injectedDelayMs: Int,
        _ status: TrialStatus,
        _ error: String?,
        elapsedNanoseconds: Int64,
        completed: [StreamMeasurement],
        cpu: ProcessResourceUsage?,
        resourcesAfter: ProcessResourceUsage?
    ) -> TrialResult {
        let requestMessages = completed.reduce(0) { $0 + $1.requestMessages }
        let responseMessages = completed.reduce(0) { $0 + $1.responseMessages }
        return TrialResult(
            benchmarkName: self.name,
            caseName: benchmarkCase.name,
            parameters: parameters,
            latencyMetric: self.latencyMetric,
            operationUnit: self.operationUnit,
            injectedDelayMs: injectedDelayMs,
            status: status,
            error: error,
            elapsedNanoseconds: elapsedNanoseconds,
            operations: requestMessages + responseMessages,
            requestMessages: requestMessages,
            responseMessages: responseMessages,
            applicationRequestBytes: completed.reduce(0) { $0 + $1.applicationRequestBytes },
            applicationResponseBytes: completed.reduce(0) { $0 + $1.applicationResponseBytes },
            latencySamplesNanoseconds: completed.flatMap(\.latencySamples),
            timeToFirstResponseNanoseconds: completed.compactMap(\.timeToFirstResponseNanoseconds),
            streamDurationNanoseconds: completed.compactMap(\.streamDurationNanoseconds),
            finalResponseLatencyNanoseconds: completed.compactMap(\.finalResponseLatencyNanoseconds),
            cpuUserNanoseconds: cpu?.cpuUserNanoseconds,
            cpuSystemNanoseconds: cpu?.cpuSystemNanoseconds,
            cpuTotalNanoseconds: cpu?.cpuTotalNanoseconds,
            maxRssBytes: resourcesAfter?.maxRssBytes
        )
    }

    /// The completed streams of one phase, plus the failure that stopped the remaining ones, if any.
    private struct StreamsOutcome {
        let completed: [StreamMeasurement]
        let failure: OperationFailure?
    }

    private static func executeStreams(
        messageCount: Int,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
        executeStream: @escaping @Sendable (Int, BenchmarkParameters, Int) async throws -> StreamMeasurement
    ) async throws -> StreamsOutcome {
        guard messageCount > 0 else { return StreamsOutcome(completed: [], failure: nil) }

        let streamCount = min(messageCount, parameters.concurrency)
        let messagesPerStream = messageCount / streamCount
        let streamsWithExtraMessage = messageCount % streamCount
        let buffer = MeasurementBuffer()

        do {
            try await withThrowingTaskGroup(of: StreamMeasurement.self) { group in
                for streamIndex in 0..<streamCount {
                    let messages = messagesPerStream + (streamIndex < streamsWithExtraMessage ? 1 : 0)
                    group.addTask {
                        let measurement = try await executeStream(messages, parameters, injectedDelayMs)
                        buffer.append(measurement)
                        return measurement
                    }
                }
                try await group.waitForAll()
            }
            return StreamsOutcome(completed: buffer.snapshot(), failure: nil)
        } catch {
            if error is CancellationError { throw error }
            return StreamsOutcome(
                completed: buffer.snapshot(),
                failure: OperationFailure(completedOperations: 0, samplesSoFar: [], cause: error)
            )
        }
    }
}

/// Collects the measurements of the streams that completed before a failure.
private final class MeasurementBuffer: @unchecked Sendable {
    private let lock = NSLock()
    private var measurements: [StreamMeasurement] = []

    func append(_ measurement: StreamMeasurement) {
        self.lock.lock()
        self.measurements.append(measurement)
        self.lock.unlock()
    }

    func snapshot() -> [StreamMeasurement] {
        self.lock.lock()
        defer { self.lock.unlock() }
        return self.measurements
    }
}
