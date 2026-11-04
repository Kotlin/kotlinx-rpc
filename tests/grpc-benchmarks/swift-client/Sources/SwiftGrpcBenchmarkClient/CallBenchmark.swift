/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore

/// A prepared operation and its application-level payload sizes per call.
struct MeasuredCall: Sendable {
    let applicationRequestBytes: Int64
    let applicationResponseBytes: Int64
    let execute: @Sendable () async throws -> Void
}

/// Reports the failure of one operation.
struct OperationFailure: Error, CustomStringConvertible, Sendable {
    let completedOperations: Int64
    let samplesSoFar: [Int64]
    let cause: Error

    var description: String {
        String(describing: self.cause)
    }
}

/// Runs a prepared operation through warmup and concurrent measurement phases.
struct CallBenchmark: Benchmark {
    let name: String
    let description: String
    let cases: [BenchmarkCase]
    let prepare: @Sendable (BenchmarkClient, BenchmarkParameters) -> MeasuredCall

    var latencyMetric: LatencyMetric { .callRoundTrip }
    var operationUnit: OperationUnit { .rpc }

    func run(
        client: BenchmarkClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int
    ) async throws -> TrialResult {
        let call = self.prepare(client, parameters)

        do {
            _ = try await Self.executeOperations(
                count: parameters.warmupOperations,
                concurrency: parameters.concurrency,
                injectedDelayMs: injectedDelayMs,
                recordLatency: false,
                call: call.execute
            )
        } catch let failure as OperationFailure {
            return self.failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                "Warmup operation \(failure.completedOperations + 1) failed: \(failure)"
            )
        }

        let clock = ContinuousClock()
        let started = clock.now
        do {
            let samples = try await Self.executeOperations(
                count: parameters.operations,
                concurrency: parameters.concurrency,
                injectedDelayMs: injectedDelayMs,
                recordLatency: true,
                call: call.execute
            )
            return TrialResult(
                benchmarkName: self.name,
                caseName: benchmarkCase.name,
                parameters: parameters,
                latencyMetric: self.latencyMetric,
                operationUnit: self.operationUnit,
                injectedDelayMs: injectedDelayMs,
                status: .completed,
                error: nil,
                elapsedNanoseconds: started.duration(to: clock.now).nanosecondsValue,
                operations: Int64(parameters.operations),
                requestMessages: nil,
                responseMessages: nil,
                applicationRequestBytes: call.applicationRequestBytes * Int64(parameters.operations),
                applicationResponseBytes: call.applicationResponseBytes * Int64(parameters.operations),
                latencySamplesNanoseconds: samples,
                timeToFirstResponseNanoseconds: [],
                streamDurationNanoseconds: [],
                finalResponseLatencyNanoseconds: []
            )
        } catch let failure as OperationFailure {
            return self.failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                "Measured operation \(failure.completedOperations + 1) of \(parameters.operations) failed: \(failure)",
                elapsedNanoseconds: started.duration(to: clock.now).nanosecondsValue,
                operations: failure.completedOperations,
                applicationRequestBytes: call.applicationRequestBytes * failure.completedOperations,
                applicationResponseBytes: call.applicationResponseBytes * failure.completedOperations,
                samples: failure.samplesSoFar
            )
        }
    }

    private func failedTrial(
        _ benchmarkCase: BenchmarkCase,
        _ parameters: BenchmarkParameters,
        _ injectedDelayMs: Int,
        _ error: String,
        elapsedNanoseconds: Int64 = 0,
        operations: Int64 = 0,
        applicationRequestBytes: Int64 = 0,
        applicationResponseBytes: Int64 = 0,
        samples: [Int64] = []
    ) -> TrialResult {
        TrialResult(
            benchmarkName: self.name,
            caseName: benchmarkCase.name,
            parameters: parameters,
            latencyMetric: self.latencyMetric,
            operationUnit: self.operationUnit,
            injectedDelayMs: injectedDelayMs,
            status: .failed,
            error: error,
            elapsedNanoseconds: elapsedNanoseconds,
            operations: operations,
            requestMessages: nil,
            responseMessages: nil,
            applicationRequestBytes: applicationRequestBytes,
            applicationResponseBytes: applicationResponseBytes,
            latencySamplesNanoseconds: samples,
            timeToFirstResponseNanoseconds: [],
            streamDurationNanoseconds: [],
            finalResponseLatencyNanoseconds: []
        )
    }

    /// Runs `count` operations with `concurrency` parallel workers and returns one latency sample
    /// per measured operation.
    ///
    /// The sample window of one operation covers the injected delay and the call, so the delay
    /// looks like slower work to the measurement. The per-worker arrays live in the shared
    /// `WorkerProgress`, so a failed trial can report the operations and samples that completed
    /// before its failure.
    private static func executeOperations(
        count: Int,
        concurrency: Int,
        injectedDelayMs: Int,
        recordLatency: Bool,
        call: @escaping @Sendable () async throws -> Void
    ) async throws -> [Int64] {
        guard count > 0 else { return [] }

        let workerCount = min(count, concurrency)
        let baseCallsPerWorker = count / workerCount
        let workersWithExtraCall = count % workerCount
        let progress = WorkerProgress(workerCount: workerCount)
        // One box per worker: the worker task is the only writer of its box.
        for workerIndex in 0..<workerCount {
            progress.boxes[workerIndex] = WorkerSamples()
        }

        do {
            return try await withThrowingTaskGroup(of: [Int64].self) { group in
                for workerIndex in 0..<workerCount {
                    group.addTask {
                        let box = progress.boxes[workerIndex]!
                        let workerCalls = baseCallsPerWorker + (workerIndex < workersWithExtraCall ? 1 : 0)
                        box.reserveCapacity(workerCalls, recordLatency: recordLatency)

                        let clock = ContinuousClock()
                        for _ in 0..<workerCalls {
                            let started = clock.now
                            if injectedDelayMs > 0 {
                                try await Task.sleep(nanoseconds: UInt64(injectedDelayMs) * 1_000_000)
                            }
                            do {
                                try await call()
                            } catch {
                                if error is CancellationError { throw error }
                                throw OperationFailure(
                                    completedOperations: 0,
                                    samplesSoFar: [],
                                    cause: error
                                )
                            }
                            box.completed += 1
                            if recordLatency {
                                box.samples.append(started.duration(to: clock.now).nanosecondsValue)
                            }
                        }
                        return box.samples
                    }
                }

                var samples: [Int64] = []
                if recordLatency {
                    samples.reserveCapacity(count)
                }
                for try await workerSamples in group {
                    samples.append(contentsOf: workerSamples)
                }
                return samples
            }
        } catch let failure as OperationFailure {
            throw OperationFailure(
                completedOperations: progress.completedOperations(),
                samplesSoFar: progress.samplesSoFar(),
                cause: failure.cause
            )
        }
    }
}

/// The samples and completed count of one worker, written only by its task.
private final class WorkerSamples: @unchecked Sendable {
    var samples: [Int64] = []
    var completed: Int64 = 0

    func reserveCapacity(_ workerCalls: Int, recordLatency: Bool) {
        if recordLatency {
            self.samples.reserveCapacity(workerCalls)
        }
    }
}

private final class WorkerProgress: @unchecked Sendable {
    var boxes: [WorkerSamples?]

    init(workerCount: Int) {
        self.boxes = Array(repeating: nil, count: workerCount)
    }

    func completedOperations() -> Int64 {
        self.boxes.compactMap { $0?.completed }.reduce(0, +)
    }

    func samplesSoFar() -> [Int64] {
        self.boxes.flatMap { box in box?.samples ?? [] }
    }
}

extension Duration {
    var nanosecondsValue: Int64 {
        Int64(self.components.seconds) * 1_000_000_000 + Int64(self.components.attoseconds / 1_000_000_000)
    }
}
