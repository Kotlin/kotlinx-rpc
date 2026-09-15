/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import GRPCCore
import GRPCProtobuf

func unaryBenchmarks() -> [any Benchmark] {
    [
        unaryBenchmark(
            name: "unary-latency",
            description: "Warm sequential unary call latency",
            cases: [
                benchmarkCase(
                    warmupCalls: 100,
                    calls: 1_000,
                    concurrency: 1,
                    requestBytes: 0,
                    responseBytes: 0
                )
            ]
        ),
        unaryBenchmark(
            name: "unary-throughput",
            description: "Warm concurrent unary call throughput",
            cases: [
                benchmarkCase(
                    warmupCalls: 100,
                    calls: 10_000,
                    concurrency: 16,
                    requestBytes: 1_024,
                    responseBytes: 1_024
                )
            ]
        ),
        unaryBenchmark(
            name: "unary-payload-sweep",
            description: "Unary request and response payload size sweep",
            cases: payloadSweepCases()
        ),
        unaryBenchmark(
            name: "unary-concurrency-sweep",
            description: "Unary concurrency sweep with empty and 1 KiB payloads",
            cases: concurrencySweepCases()
        ),
    ]
}

private func unaryBenchmark(
    name: String,
    description: String,
    cases: [BenchmarkCase]
) -> any Benchmark {
    CallBenchmark(name: name, description: description, cases: cases) { client, parameters in
        let service = Grpc_Testing_BenchmarkService.Client(wrapping: client)
        let request = unaryRequest(
            requestBytes: parameters.requestBytes,
            responseBytes: parameters.responseBytes
        )

        return MeasuredCall(
            applicationBytes: Int64(parameters.requestBytes) + Int64(parameters.responseBytes),
            execute: {
                let response = try await service.unaryCall(request)
                guard response.payload.body.count == parameters.responseBytes else {
                    throw UnexpectedResponseSize(
                        expected: parameters.responseBytes,
                        actual: response.payload.body.count
                    )
                }
            }
        )
    }
}

private struct PayloadSweepSize {
    let name: String
    let bytes: Int
    let warmupCalls: Int
    let calls: Int
}

private let payloadSweepSizes = [
    PayloadSweepSize(name: "64b", bytes: 64, warmupCalls: 100, calls: 10_000),
    PayloadSweepSize(name: "1k", bytes: 1_024, warmupCalls: 100, calls: 10_000),
    PayloadSweepSize(name: "64k", bytes: 64 * 1_024, warmupCalls: 20, calls: 2_000),
    PayloadSweepSize(name: "1m", bytes: 1_024 * 1_024, warmupCalls: 5, calls: 200),
    PayloadSweepSize(
        name: "near-4m",
        bytes: 4 * 1_024 * 1_024 - 1_024,
        warmupCalls: 2,
        calls: 50
    ),
]

private func payloadSweepCases() -> [BenchmarkCase] {
    let symmetric = payloadSweepSizes.map { size in
        payloadCase(
            name: "symmetric-\(size.name)",
            size: size,
            requestBytes: size.bytes,
            responseBytes: size.bytes
        )
    }
    let upload = payloadSweepSizes.dropFirst().map { size in
        payloadCase(
            name: "upload-\(size.name)",
            size: size,
            requestBytes: size.bytes,
            responseBytes: payloadSweepAnchorBytes
        )
    }
    let download = payloadSweepSizes.dropFirst().map { size in
        payloadCase(
            name: "download-\(size.name)",
            size: size,
            requestBytes: payloadSweepAnchorBytes,
            responseBytes: size.bytes
        )
    }
    return symmetric + upload + download
}

private func payloadCase(
    name: String,
    size: PayloadSweepSize,
    requestBytes: Int,
    responseBytes: Int
) -> BenchmarkCase {
    benchmarkCase(
        name: name,
        warmupCalls: size.warmupCalls,
        calls: size.calls,
        concurrency: 1,
        requestBytes: requestBytes,
        responseBytes: responseBytes
    )
}

private func concurrencySweepCases() -> [BenchmarkCase] {
    [("empty", 0), ("1k", 1_024)].flatMap { payloadName, payloadBytes in
        [1, 2, 4, 8, 16, 32, 64, 128].map { concurrency in
            benchmarkCase(
                name: "\(payloadName)-c\(concurrency)",
                warmupCalls: max(100, concurrency * 10),
                calls: max(10_000, concurrency * 1_000),
                concurrency: concurrency,
                requestBytes: payloadBytes,
                responseBytes: payloadBytes
            )
        }
    }
}

private func benchmarkCase(
    name: String = "default",
    warmupCalls: Int,
    calls: Int,
    concurrency: Int,
    requestBytes: Int,
    responseBytes: Int
) -> BenchmarkCase {
    BenchmarkCase(
        name: name,
        parameters: try! BenchmarkParameters(
            warmupCalls: warmupCalls,
            calls: calls,
            concurrency: concurrency,
            requestBytes: requestBytes,
            responseBytes: responseBytes
        )
    )
}

private let payloadSweepAnchorBytes = 64

private func unaryRequest(requestBytes: Int, responseBytes: Int) -> Grpc_Testing_SimpleRequest {
    var payload = Grpc_Testing_Payload()
    payload.type = .compressable
    payload.body = Data((0..<requestBytes).lazy.map { UInt8($0 % 251) })

    var request = Grpc_Testing_SimpleRequest()
    request.responseType = .compressable
    request.responseSize = Int32(responseBytes)
    request.payload = payload
    return request
}

private struct UnexpectedResponseSize: Error, CustomStringConvertible, Sendable {
    let expected: Int
    let actual: Int

    var description: String {
        "Expected \(self.expected) response bytes, got \(self.actual)"
    }
}
